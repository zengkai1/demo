package com.example.demo.config.jwt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.demo.co.shiro.AppShiroUser;
import com.example.demo.co.shiro.UserContext;
import com.example.demo.config.shiro.CustomRealm;
import com.example.demo.config.shiro.CustomToken;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.redis.RedisLockUtil;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.RequestIpUtils;
import com.example.demo.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 *  Jwt的Fiter，集成自Shiro的BasicHttpAuthenticationFilter
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/30 10:57
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 检查是否需要更新Token
     * @param authorization
     * @param currentTimeMillis
     * @return
     */
    private boolean refreshCheck(String authorization, Long currentTimeMillis) {
        String tokenMillis = JwtUtil.getClaim(authorization, SecurityConstants.CURRENT_TIME_MILLIS);
        if (currentTimeMillis - Long.parseLong(tokenMillis) > (jwtProperties.getRefreshCheckTime() * 60 * 1000L)) {
            return true;
        }
        return false;
    }


    /**
     * 检测Header里Authorization，判断是否登陆
     * @param request
     * @param response
     * @param mappedValue
     * @return 是否登陆
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest request1 = (HttpServletRequest) request;
        System.out.println("请求路径："+  request1.getRequestURI());
        if (isLoginAttempt(request,response)){
            try {
                this.executeLogin(request,response);
            } catch (Exception e) {
                String msg = e.getMessage();
                Throwable throwable = e.getCause();
                if (throwable != null && throwable instanceof SignatureVerificationException) {
                    msg = "Token或者密钥不正确(" + throwable.getMessage() + ")";
                } else if (throwable != null && throwable instanceof TokenExpiredException) {
                    // AccessToken已过期
                    if (this.refreshToken(request, response)) {
                        return true;
                    } else {
                        msg = "Token已过期(" + throwable.getMessage() + ")";
                    }
                } else {
                    if (throwable != null) {
                        msg = throwable.getMessage();
                    }
                }
                this.illgalRequest(response, msg);
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        String token = this.getAuthzHeader(request);
        return token != null;
    }

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return this.isAccessAllowed(request, response, mappedValue) || this.onAccessDenied(request, response, mappedValue);
    }

    /**
     * 401非法请求
     * @param response
     * @param msg
     */
    private void illgalRequest(ServletResponse response, String msg) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = httpServletResponse.getWriter();
            Result result = new Result();
            result.setCode(StatusCode.ILLEGAL.getCode()).setMsg(msg).setDescription("非法请求").setData(null);
            out.append(JSON.toJSONString(result));
        } catch (IOException e) {
            LOGGER.error("返回Response信息出现IOException异常:" + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 登陆验证
     * @param request
     * @param response
     * @throws Exception
     * @return 是否登陆成功
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        //登陆暂时放行
        String authorization = servletRequest.getHeader(SecurityConstants.REQUEST_AUTH_HEADER);
        CustomToken customToken = new CustomToken(authorization);
        //提交realm进行登入，如果错误则会抛出异常被捕获
        Subject subject = SecurityUtils.getSubject();
        subject.login(customToken);
        //绑定上下文
        String account = JwtUtil.getClaim(authorization,SecurityConstants.ACCOUNT);
        AppShiroUser appShiroUser = new AppShiroUser(account,customToken.getPrincipal().toString(), RequestIpUtils.getIpAddr(servletRequest));
        UserContext userContext= new UserContext(appShiroUser);
        //检查是否需要更好token，需要则重新颁发
        refreshTokenIfNeed(account,authorization,response);
        //如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 检查是否需要刷新token
     * @param account ： 账号
     * @param authorization ： 原token
     * @param response ： 响应
     * @return 是否需要刷新token
     */
    private boolean refreshTokenIfNeed(String account, String authorization, ServletResponse response) {
        String lockKey = SecurityConstants.PREFIX_SHIRO_REFRESH_TOKEN + account;
        try {
            Long currentTimeMillis= System.currentTimeMillis();
            //检查刷新规则
            if(this.refreshCheck(authorization,currentTimeMillis)){
                RedisLockUtil.lock(lockKey,jwtProperties.getRefreshCheckTime(),TimeUnit.MINUTES);
                LOGGER.info(String.format("为账户%s颁发新的令牌", account));
                String newToken = JwtUtil.sign(account, String.valueOf(currentTimeMillis));
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader(SecurityConstants.REQUEST_AUTH_HEADER, newToken);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", SecurityConstants.REQUEST_AUTH_HEADER);
                RedisLockUtil.unlock(lockKey);
                return true;
            }
        }catch (Exception e){
            LOGGER.info("检查是否需要刷新token异常:{}",e.getMessage());
            RedisLockUtil.unlock(lockKey);
        }
        return false;
    }

    /**
     * 重写 onAccessDenied 方法，避免父类中调用再次executeLogin
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        this.sendChallenge(request, response);
        return false;
    }

    /**
     * 刷新AccessToken，判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
     * @param request
     * @param response
     * @return boolean
     */
    public boolean refreshToken(ServletRequest request,ServletResponse response){
        //获取AccessToken(shiro中getAuthzHeader方法已实现)
        String token = this.getAuthzHeader(request);
        //获取当前token的账号信息
        String account = JwtUtil.getClaim(token, SecurityConstants.ACCOUNT);
        String refreshTokenCacheKey = SecurityConstants.PREFIX_SHIRO_REFRESH_TOKEN + account;
        //判断Redis中RefreshToken是否存在
        String currentTimeMillsRedis  = (String)redisTemplate.opsForValue().get(refreshTokenCacheKey);
        if (StrUtil.isNotEmpty(currentTimeMillsRedis)){
            //相比如果一致，进行AccessToken刷新
            String tokenMillis = JwtUtil.getClaim(token, SecurityConstants.ACCOUNT);
            if (tokenMillis.equals(currentTimeMillsRedis)){
                //设置RefreshToken中的时间戳为当前最新时间戳
                String currentTime = String.valueOf(System.currentTimeMillis());
                Integer refreshTokenExpireTime = jwtProperties.getRefreshTokenExpireTime();
                redisTemplate.opsForValue().set(refreshTokenCacheKey,currentTime,refreshTokenExpireTime, TimeUnit.SECONDS);
                //刷新AccessToken为当前最新时间戳
                token = JwtUtil.sign(account, currentTime);
                //使用AccessToken再次提交shiroRealm进行认证，如果没有抛出异常则登入成功，返回true
                CustomToken customToken = new CustomToken(token);
                this.getSubject(request,response).login(customToken);
                //设置响应的Header头新Token
                HttpServerResponse httpServerResponse = (HttpServerResponse) response;
                httpServerResponse.setHeader(SecurityConstants.REQUEST_AUTH_HEADER,token);
                httpServerResponse.setHeader(SecurityConstants.ACCESS_CONTROL_EXPOSE,SecurityConstants.REQUEST_AUTH_HEADER);
                return true;
            }
        }
        //未过期
        return false;
    }


}
