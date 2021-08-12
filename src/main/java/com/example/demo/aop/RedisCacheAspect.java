package com.example.demo.aop;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.example.demo.annotation.CacheEvict;
import com.example.demo.annotation.Cacheable;
import com.example.demo.constants.StatusCode;
import com.example.demo.exception.ZKCustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  redis缓存切面
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/8 15:16
 */
@Aspect
@Component
@Slf4j
public class RedisCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 读取数据/添加缓存key
     * @param pjp
     * @return
     */
    @Around(value = "@annotation(cacheable)")
    public Object cacheable(ProceedingJoinPoint pjp, Cacheable cacheable) {
        //定义返回数据result
        Object result = null;
        //判断是否开启缓存
        if (Boolean.FALSE.equals(cacheable.cacheEnable())) {
            try {
                result = pjp.proceed();
            } catch (Throwable e) {
                log.error("redis缓存异常，{}", ExceptionUtil.stacktraceToString(e));
            }
            //返回数据
            return result;
        }
        //获取当前方法
        Method method = getMethod(pjp);
        //获取返回值
        Class<?> returnType = method.getReturnType();
        //获取方法上的注解
        cacheable = method.getAnnotation(Cacheable.class);
        //获取方法的返回值类型，让缓存可以返回正确的类型
        String key =parseKey(cacheable.keyPrefix(),cacheable.fieldKey(),method,pjp.getArgs());
        //先根据key查询redis缓存
        result =  redisTemplate.opsForValue().get(key);
        //如果是基本类型直接返回即可
        if (isDirectOutPut(result)){
            return result;
        }
        //将result转成json进行解析
        JSON parse = JSONUtil.parse(result);
        //若不存在则存入缓存
        if (isBlankOrEmpty(parse,returnType)) {
            try {
                //获取数据
                result = pjp.proceed();
                String code = JSONUtil.parseObj(result).getStr("code");
                if (StrUtil.isNotBlank(code) && code.equals(String.valueOf(StatusCode.SUCCESS.getCode()))){
                    redisTemplate.opsForValue().set(key, result, cacheable.expireTime(), TimeUnit.MINUTES);
                }
            } catch (Throwable e) {
                throw new ZKCustomException(StatusCode.FAILURE.getCode(),String.format("由redis切面获取数据异常:%s",e.getMessage()));
            }
        }
        //返回数据
        return result;
    }

    /**
     * 过滤以下常用类型,直接输出无需解析
     * @param result
     * @return
     */
    private boolean isDirectOutPut(Object result) {
        if (result instanceof String){
            return true;
        }
        if (result instanceof Integer){
            return true;
        }
        if (result instanceof Boolean){
            return true;
        }
        if (result instanceof Double){
            return true;
        }
        if (result instanceof Long){
            return true;
        }
        if (result instanceof Short){
            return true;
        }
        if (result instanceof List){
            return true;
        }
        if (result instanceof Map){
            return true;
        }
        return false;
    }

    /**
     * 判断对象是否为空
     * @param parse ： 判断对象JSON
     * @return ：boolean
     */
    private  boolean isBlankOrEmpty(JSON parse,Class<?> returnType) {
        //定义返回数据result
        if (Objects.isNull(parse)){
            return true;
        }
        //对象转json判断是否为空
        if (parse.toString().equals("{}") || parse.toString().equals("[{}]")){
            return true;
        }
        Object object = JSONUtil.toBean(JSONUtil.parseObj(parse), returnType);
        //反射获取类属性，排除对象默认属性
        String className = object.getClass().getName();
        try {
            if (object.equals(Class.forName(className).newInstance())){
                return true;
            }
        } catch (Exception e) {
            log.error("通过反射获取类属性异常：{}",e);
        }
        return false;
    }

    /**
     *  清除缓存key
     */
    @After(value = "@annotation(cacheEvict)")
    public void CacheEvict(JoinPoint pjp, CacheEvict cacheEvict) {
        //定义返回数据result
        Object result = null;
        //缓存开关
        Boolean cacheEnable = true;
        //判断是否开启缓存
        if (!cacheEnable) {
            //返回数据
            return ;
        }
        //获取当前方法
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        //获取方法上的注解
        cacheEvict = method.getAnnotation(CacheEvict.class);
        //获取方法的返回值类型，让缓存可以返回正确的类型
        String key =parseKey(cacheEvict.keyPrefix(),cacheEvict.fieldKey(),method,pjp.getArgs());
        //若不存在则存入缓存
        if (Objects.isNull(result)) {
            //删除key
            redisTemplate.delete(key);
        }
    }

    /**
     *  获取被拦截方法对象
     *
     */
    public Method getMethod(ProceedingJoinPoint pjp){
        //获取参数的类型
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        return method;
    }

    /**
     * 获取缓存的key
     * key 定义在注解上，支持SPEL表达式
     * @return
     */
    private String parseKey(String key,String fieldKey,Method method,Object [] args) {
        //若传入的为固定Str作为key，则无需解析
        if (!fieldKey.startsWith("#")){
            return key + fieldKey;
        }
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u =
                new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);

        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return key+parser.parseExpression(fieldKey).getValue(context, String.class);
    }
}