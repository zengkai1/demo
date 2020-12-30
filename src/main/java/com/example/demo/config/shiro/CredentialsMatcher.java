package com.example.demo.config.shiro;

import com.example.demo.util.MD5SaltUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p>
 *  自定义shiro密码比较器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 17:17
 */
@Configuration
public class CredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //从token中获取密码信息
        UsernamePasswordToken uToken = (UsernamePasswordToken) token;
        //用户输入的密码
        String inputPassword = new String( uToken.getPassword());
        //数据库中存储的密码
        String dbPassword = (String) info.getCredentials();
        //密码比对
        return  MD5SaltUtil.verify(inputPassword,dbPassword);
    }
}
