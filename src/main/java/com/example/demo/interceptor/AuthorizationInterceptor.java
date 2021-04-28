package com.example.demo.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.annotation.Authorization;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>
 *  权限拦截器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年04月28日
 */
@Slf4j
@Component
public class AuthorizationInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Authorization authorization = method.getAnnotation(Authorization.class);

            //权限校验
            if (authorization != null) {
                String value = authorization.value();
                //todo 获取用户权限列表
                List<String> permission = Lists.newArrayList();
                log.info("user permission {} ", JSONObject.toJSONString(permission));
                if (permission == null || permission.size() == 0) {
                    //throw new RuntimeException("没有权限！");
                    return true;
                }
                if (StrUtil.isEmpty(value)) {
                    return false;
                }
                if (!permission.contains(value)) {
                    throw new RuntimeException("没有权限！");
                }
            }
        }
        return true;

    }
}
