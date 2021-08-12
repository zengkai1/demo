package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.co.User;
import com.example.demo.constants.interfaces.SecurityConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/14 10:05
 */
@SpringBootTest
public class ShiroTest {

    SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getToken(){
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        //生成token
        String account = "zengkai";
        //账号加JWT私钥加密
        String secret = account + "demokey";
        //此处过期时间/毫秒
        Date date = new Date(System.currentTimeMillis() + 1440 * 60 * 1000L);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String sign = JWT.create().withClaim(SecurityConstants.ACCOUNT, account)
                .withClaim(SecurityConstants.CURRENT_TIME_MILLIS, currentTimeMillis)
                .withExpiresAt(date)
                .sign(algorithm);
        redisTemplate.opsForValue().set("demo:security:refresh_token:"+account,currentTimeMillis,120, TimeUnit.MINUTES);
        System.out.println(sign);
    }

    @Test
    public void testAuthentication() {
        simpleAccountRealm.addAccount("wmyskxz", "123456");
        // 1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        // 2.主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager); // 设置SecurityManager环境
        Subject subject = SecurityUtils.getSubject(); // 获取当前主体

        UsernamePasswordToken token = new UsernamePasswordToken("wmyskxz", "123456");
        subject.login(token); // 登录

        // subject.isAuthenticated()方法返回一个boolean值,用于判断用户是否认证成功
        System.out.println("isAuthenticated:" + subject.isAuthenticated()); // 输出true

        subject.logout(); // 登出

        System.out.println("isAuthenticated:" + subject.isAuthenticated()); // 输出false
    }

    @Test
    public void testEquals(){
        try {
            User a = null;
            String s2 = String.valueOf(a);
            System.out.println("String.valueOf："+s2);
            String s = a.toString();
            System.out.println("toString："+s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testEquals2(){
        long start = System.currentTimeMillis();
        for (Integer a = 0;a<100000;a++){
            String.valueOf(a);
        }
        System.out.println("time："+(System.currentTimeMillis()-start)+" ms");

        long start2 = System.currentTimeMillis();
        for (Integer b = 0;b<100000;b++){
            b.toString();
        }
        System.out.println("time："+(System.currentTimeMillis()-start2)+" ms");
    }

}

