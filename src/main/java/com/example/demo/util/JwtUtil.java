package com.example.demo.util;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.config.jwt.JwtProperties;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.exception.ZKCustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * <p>
 *  jwt工具类 JWT(JSON Web Token),
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/30 14:21
 */
@Component
@Slf4j
public class JwtUtil {

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    private static JwtUtil jwtUtil;

    /**
     * 初始化方法
     *
     * @PostConstruct该注解被用来修饰一个非静态的void（）方法。
     * 被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，
     * 并且只会被服务器执行一次。
     * PostConstruct在构造函数之后执行，init（）方法之前执行。
     */
    @PostConstruct
    public void init(){
        jwtUtil = this;
        jwtUtil.jwtProperties = this.jwtProperties;
    }

    /**
     * 校验token是否正确
     * @param token ： token
     * @return boolean
     */
    public static boolean verify(String token){
        token = token.replace(SecurityConstants.TOKEN_PREFIX,"");
        String account = getClaim(token, SecurityConstants.ACCOUNT);
        String secret = account + jwtUtil.jwtProperties.getSecretKey();;
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withClaim(SecurityConstants.ACCOUNT, account)
                .build();
        verifier.verify(token);
        return true;
    }

    /**
     * 生成签名,5min后过期
     * @param account
     * @param currentTimeMillis
     * @return
     */
    public static  String sign(String account, String currentTimeMillis){
        //账号加JWT私钥加密
        String secret = account + jwtUtil.jwtProperties.getSecretKey();
        //此处过期时间/毫秒
        Date date = new Date(System.currentTimeMillis() + jwtUtil.jwtProperties.getTokenExpireTime() * 60 * 1000L);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create().withClaim(SecurityConstants.ACCOUNT,account)
                .withClaim(SecurityConstants.CURRENT_TIME_MILLIS,currentTimeMillis)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @param token
     * @param claim
     * @return
     */
    public static String getClaim(String token,String claim){
        try{
            if (StrUtil.isBlank(token)){
                throw new ZKCustomException("未携带请求token！");
            }
            token = token.replace(SecurityConstants.TOKEN_PREFIX,"");
            DecodedJWT jwt = JWT.decode(token);
            String str = jwt.getClaim(claim).asString();
            return str;
        }catch (JWTDecodeException e){
            log.info("用户token解析异常:{}",e.getMessage());
            throw new ZKCustomException("用户token解析异常!");
        }
    }
}
