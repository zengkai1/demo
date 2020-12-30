package com.example.demo.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.annotation.LimitRequestAnnotation;
import com.example.demo.constants.enums.LimitTypeEnum;
import com.example.demo.constants.enums.ResultEnum;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.util.RequestIpUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *限流AOP拦截器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/9 17:18
 */
@Log4j2
@Aspect
@Component
@Order(1)
public class LimitRequestInterceptor {

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;


    /*    @SneakyThrows的用法比较简单，其实就是对于异常的一个整理，将checked exception 看做unchecked exception，
        不处理，直接扔掉。 减少了到处写catch的不便利性。比如在线程中，catch所有异常，再比如在一些不太可能发生异常
        的地方，但是你又必须cache checked exception的地方使用这个annotation会显得代码比较规整，易读。
        或许也会显得高大上一点吧*/
    @SneakyThrows
    @Around("@annotation(limitRequestAnnotation)")
    public Object limitInterceptor(ProceedingJoinPoint proceedingJoinPoint, LimitRequestAnnotation limitRequestAnnotation){

        //获取当前请求HttpServletRequest
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取请求头
        String requestHeader = request.getHeader("Authorization");

        Object proceed = null;

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        //如果annotation为空 直接放行执行程序
        if (Objects.isNull(limitRequestAnnotation)){
            try {
                proceed = proceedingJoinPoint.proceed();
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
            return proceed;
        }
        //获取当前请求用户ID
        String userId = "zengkai";
        //获取接口注解限流类型\限制时间\限制次数
        LimitTypeEnum limitType = limitRequestAnnotation.limitType();
        int maxCount = limitRequestAnnotation.count();
        String key;
        String value = limitRequestAnnotation.value();
        int limitPeriod = limitRequestAnnotation.period();
        //获取当前时间
        String dateTime = DateUtil.offsetSecond(DateUtil.date(), (int) limitPeriod).toString();
        Integer currentCount = 1;
        switch(limitType){
            //IP限流
            case IP:
                key =  value + RequestIpUtils.getIpAddr(request);
                break;
            //客户自定义限流
            case CUSTOMER:
                key = value + userId;
                break;
            default:
                key = StringUtils.upperCase(method.getName());
        }
        //查询当前计数
        JSONObject str = (JSONObject) redisTemplate.opsForValue().get(key);
        Map<String,Object> result = (Map<String,Object>)str;
        if (CollUtil.isEmpty(result)){
            Map map = new HashMap();
            map.put("currentCount", currentCount);
            map.put("dateTime", dateTime);
            redisTemplate.opsForValue().set(key, JSONUtil.parse(map) ,limitPeriod, TimeUnit.SECONDS);
            result = map;
        }
        if (CollUtil.isNotEmpty(result)){
            Integer count = (Integer) (result.get("currentCount"));
            if (count <= maxCount){
                log.info("当前已查询{}次",count);
                count++;
                Map map = new HashMap();
                map.put("currentCount", count);
                map.put("dateTime", dateTime);
                redisTemplate.opsForValue().set(key, JSONUtil.parse(map),limitPeriod, TimeUnit.SECONDS);
                return proceedingJoinPoint.proceed();
            }else {
                //获取锁定时间
                String lockTime = (String)result.get("dateTime");
                //当前解锁倒计时
                long between = DateUtil.between(DateUtil.parse(lockTime), DateUtil.parse(new DateTime().toString()), DateUnit.SECOND);
                String formatBetween = DateUtil.formatBetween(between * 1000, BetweenFormatter.Level.SECOND);
                throw new ZKCustomException(ResultEnum.ERROR_CODE.getCode(),String.format(limitRequestAnnotation.exceptionMsg()+"离解锁还剩:%s",formatBetween));
            }
        }
        return  proceedingJoinPoint.proceed();
    }
}
