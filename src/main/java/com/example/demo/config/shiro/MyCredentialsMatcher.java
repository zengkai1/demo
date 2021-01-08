package com.example.demo.config.shiro;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.DemoConstants;
import com.example.demo.constants.interfaces.KeyPrefixConstants;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.util.DateTimeUtil;
import com.example.demo.util.MD5SaltUtil;
import com.example.demo.util.RequestIpUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public class MyCredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //从token中获取密码信息
        UsernamePasswordToken uToken = (UsernamePasswordToken) token;
        //用户输入的密码
        String inputPassword = new String( uToken.getPassword());
        //数据库中存储的密码
        String dbPassword = (String) info.getCredentials();
        //密码比对
        boolean verify = MD5SaltUtil.verify(inputPassword, dbPassword);
        return verify;
    }
}
