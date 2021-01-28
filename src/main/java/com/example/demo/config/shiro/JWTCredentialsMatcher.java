package com.example.demo.config.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.demo.co.LoginUser;
import com.example.demo.util.JwtUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 *  JWT凭证比较器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021/1/11 14:55
 */
public class JWTCredentialsMatcher implements CredentialsMatcher {

    private final Logger log = LoggerFactory.getLogger(JWTCredentialsMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        String token = (String) authenticationToken.getCredentials();
        String account = authenticationInfo.getCredentials().toString();
        if (JwtUtil.verify(token)){
            return true;
        }
        log.error("Token Error,account:{}",account);
        return false;
    }
}
