package com.example.demo.config.jwt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.Result;
import jodd.util.StringUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 *  自定义系统登出拦截器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/30 16:58
 */
public class SystemLogoutFilter extends LogoutFilter {

    private static final Logger logger = LoggerFactory.getLogger(SystemLogoutFilter.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String authorization = httpServletRequest.getHeader(SecurityConstants.REQUEST_AUTH_HEADER);
            String account = JwtUtil.getClaim(authorization, SecurityConstants.ACCOUNT);

            if(StringUtil.isNotEmpty(account)){
                // 清除可能存在的Shiro权限信息缓存
                String tokenKey = SecurityConstants.PREFIX_SHIRO_CACHE + account;
                String token = (String)redisTemplate.opsForValue().get(tokenKey);
                if (StrUtil.isNotEmpty(token)) {
                    redisTemplate.delete(tokenKey);
                }
            }
            subject.logout();
        } catch (Exception ex) {
            logger.error("退出登录错误",ex);
            this.writeResult(response);
        }
        //不执行后续的过滤器
        return true;
    }

    private void writeResult(ServletResponse response){
        //响应Json结果
        PrintWriter out = null;
        try {
            out = response.getWriter();
            Result result = new Result(StatusCode.SUCCESS.getCode(),"退出登陆");
            out.append(JSONUtil.toJsonStr(JSONUtil.parse(result)));
        } catch (IOException e) {
            logger.error("返回Response信息出现IOException异常:" + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }
}
