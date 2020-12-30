package com.example.demo.filter;

import com.example.demo.config.springmvc.MyHttpServletRequestWrapper;
import com.example.demo.util.HttpHelper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *    个人请求过滤器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 11:07
 */
@Configuration
public class MyRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //防止流读取一次后就没有了，需要将流继续写下去，提供后续使用
        ServletRequest requestWrapper = new MyHttpServletRequestWrapper(httpServletRequest);
        String json = HttpHelper.getBodyString(requestWrapper);
        filterChain.doFilter(requestWrapper,httpServletResponse);
    }
}
