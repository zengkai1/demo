package com.example.demo.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

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

    public CustomToken(String token){
        this.token = token;
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
