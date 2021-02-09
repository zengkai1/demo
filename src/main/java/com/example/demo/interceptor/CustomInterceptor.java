package com.example.demo.interceptor;

import cn.hutool.core.util.StrUtil;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <p>
 *  自定义拦截器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 11:08
 */
@Configuration
public class CustomInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CustomInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if (request.getRequestURI().equals("/error")){
            logger.info("已放行:{}",request.getRequestURI());
            return true;
        }
        //鉴权
        String token = request.getHeader(SecurityConstants.REQUEST_AUTH_HEADER);
        if (StrUtil.isEmpty(token)){
            throw new ZKCustomException(StatusCode.ILLEGAL.getCode(),"未携带请求凭证!");
        }
        //获取请求参数
        ServletInputStream inputStream = request.getInputStream();
        byte[] bodyBytes = StreamUtils.copyToByteArray(inputStream);
        String body = new String(bodyBytes, request.getCharacterEncoding());
        logger.info("自定义拦截器开启，请求路径: {}",request.getRequestURI());
        if (Objects.nonNull(request.getQueryString())){
            logger.info("请求参数:{}",request.getQueryString());
        }
        if (StrUtil.isNotEmpty(body)){
            logger.info("请求体：{}",body);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //从请求头中获取数据
        logger.info("自定义拦截器结束，请求路径: {} ", request.getRequestURI());
    }
}
