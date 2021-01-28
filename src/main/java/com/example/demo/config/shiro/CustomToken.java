package com.example.demo.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  自定义token
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/30 9:57
 */
public class CustomToken implements AuthenticationToken {

    /**
     * 用户准入token
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名
     * @param token
     */
    public CustomToken(String token){
        this.token = token;
    }

    public CustomToken(String username,String password){
        this.password = password;
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }

    public String getUsername(){
        return this.username;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
