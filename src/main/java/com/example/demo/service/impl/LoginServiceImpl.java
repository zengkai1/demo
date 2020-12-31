package com.example.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.example.demo.co.LoginUser;
import com.example.demo.co.Permissions;
import com.example.demo.co.Role;
import com.example.demo.co.User;
import com.example.demo.config.jwt.JwtProperties;
import com.example.demo.config.shiro.CustomToken;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.service.LoginService;
import com.example.demo.util.AppSecurityUtils;
import com.example.demo.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *    登陆服务实现类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:06
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void login(LoginUser user,HttpServletResponse response) {
        //进行验证
        UsernamePasswordToken token = new
                UsernamePasswordToken(user.getUsername(),user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        loginSuccess(user.getUsername(),response);
/*        subject.checkRole("admin");
        subject.checkPermissions("query", "add");*/
    }

    /**
     * 登录后更新缓存，生成token，设置响应头部信息
     * @param account
     * @param response
     */
    private void loginSuccess(String account, HttpServletResponse response){

        String currentTimeMillis = String.valueOf(System.currentTimeMillis());

        // 清除可能存在的Shiro权限信息缓存
        String tokenKey = SecurityConstants.PREFIX_SHIRO_CACHE + account;
        String  oldToken = (String)redisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isNotEmpty(oldToken)) {
            redisTemplate.delete(redisTemplate);
        }
        //更新RefreshToken缓存的时间戳
        String refreshTokenKey= SecurityConstants.PREFIX_SHIRO_REFRESH_TOKEN + account;
        String refreshToken = (String)redisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isNotEmpty(refreshToken)) {
            redisTemplate.delete(refreshTokenKey);
            redisTemplate.opsForValue().set(refreshTokenKey,currentTimeMillis,jwtProperties.getRefreshTokenExpireTime(), TimeUnit.MINUTES);
        }else{
            redisTemplate.opsForValue().set(refreshTokenKey,currentTimeMillis,jwtProperties.getRefreshTokenExpireTime(), TimeUnit.MINUTES);
        }
        //生成token
        String token = JwtUtil.sign(account, currentTimeMillis);
        //写入header
        response.setHeader(SecurityConstants.REQUEST_AUTH_HEADER, token);
        response.setHeader(SecurityConstants.ACCESS_CONTROL_EXPOSE, SecurityConstants.REQUEST_AUTH_HEADER);
        //用户认证信息
     //   Subject subject = SecurityUtils.getSubject();
      //  subject.login(new CustomToken(token));
    }
}
