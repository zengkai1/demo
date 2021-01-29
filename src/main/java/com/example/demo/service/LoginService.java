package com.example.demo.service;

import com.example.demo.co.LoginUser;
import com.example.demo.co.User;
import com.example.demo.co.shiro.UserContext;
import com.example.demo.util.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  登陆服务
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:07
 */
public interface LoginService {

    /**
     * 用户登陆
     * @param user : 用户信息
     */
    Boolean login(LoginUser user, HttpServletRequest request, HttpServletResponse response);

    /**
     * 刷新token
     * @param accessToken ： 准入token
     * @return 刷新结果
     */
    Result<String> refreshToken(String accessToken);

    /**
     * 根据用户名获取token信息
     * @param username 用户名
     * @return token
     */
    Result<String> getTokenByAccount(String username);
}
