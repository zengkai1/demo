package com.example.demo.config.jwt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.demo.co.LoginUser;
import com.example.demo.config.shiro.CustomToken;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.Result;
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
     * 检测Header里Authorization，判断是否登陆
     * @param request
     * @param response
     * @param mappedValue
     * @return 是否登陆
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
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
                this.illgalRequest(request, response, msg);
                return false;
            }
        }
        return false;
    }

    /**
     * 401非法请求
     * @param request
     * @param response
     * @param msg
     */
    private void illgalRequest(ServletRequest request, ServletResponse response, String msg) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = httpServletResponse.getWriter();

            Result result = new Result();
            result.setCode(StatusCode.ILLEGAL.getCode()).setMsg(msg);
            out.append(JSONUtil.toJsonStr(result));
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
        String authorization = servletRequest.getHeader(SecurityConstants.REQUEST_AUTH_HEADER);
        CustomToken customToken = new CustomToken(authorization);
        //提交realm进行登入，如果错误则会抛出异常被捕获
        getSubject(request,response).login(customToken);
        //绑定上下文
        String account = JwtUtil.getClaim(authorization,SecurityConstants.ACCOUNT);
        //TODO
        //UserContext userContext= new UserContext(new LoginUser(account));
        //如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 刷新AccessToken，判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
     * @param request
     * @param response
     * @return boolean
     */
    private boolean refreshToken(ServletRequest request,ServletResponse response){
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
