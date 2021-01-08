
package com.example.demo.config.shiro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.demo.co.LoginUser;
import com.example.demo.co.Permissions;
import com.example.demo.co.Role;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.DemoConstants;
import com.example.demo.constants.interfaces.KeyPrefixConstants;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.service.UserService;
import com.example.demo.util.DateTimeUtil;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.RequestIpUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  自定义主体
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:11
 */

@Configuration
@Log4j2
public class LoginRealm extends AuthorizingRealm{

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    //将自己的验证方式加入容器
    public LoginRealm() {
        // 设置凭证比较器
        MyCredentialsMatcher credentialsMatcher = new MyCredentialsMatcher();
        setCredentialsMatcher(credentialsMatcher);
        setAuthenticationTokenClass(UsernamePasswordToken.class);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * @MethodName doGetAuthorizationInfo
     * @Description 权限配置类
     * @Param [principalCollection]
     * @Return AuthorizationInfo
     * @Author zengkai
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        String name = (String) principalCollection.getPrimaryPrincipal();
        //查询用户名称
        LoginUser user = userService.qryUserByUsername(name);
        if (Objects.nonNull(user)){
            throw new ZKCustomException(StatusCode.NON.getCode(),"用户不存在");
        }
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        if (CollUtil.isNotEmpty(user.getRoles())){
            for (Role role : user.getRoles()) {
                //添加角色
                simpleAuthorizationInfo.addRole(role.getRoleName());
                //添加权限
                for (Permissions permissions : role.getPermissions()) {
                    simpleAuthorizationInfo.addStringPermission(permissions.getPermissionsName());
                }
            }
        }
        return simpleAuthorizationInfo;
    }

    /**
     * @MethodName doGetAuthenticationInfo
     * @Description 认证配置类
     * @Param [authenticationToken]
     * @Return AuthenticationInfo
     * @Author zengkai
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        if (Objects.isNull(authenticationToken.getPrincipal())) {
            return null;
        }
        //获取用户信息
        String token = authenticationToken.getPrincipal().toString();
        LoginUser user = userService.qryUserByUsername(token);
        if (user == null) {
            //这里返回后会报出对应异常
            throw new ZKCustomException(StatusCode.NON.getCode(), "用户不存在");
        }
        if (authenticationToken instanceof UsernamePasswordToken) {
            //这里验证authenticationToken和simpleAuthenticationInfo的信息
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(token, user.getPassword().toString(), "loginRealm");
            return simpleAuthenticationInfo;
        }else if (authenticationToken instanceof CustomToken) {
            String account = JwtUtil.getClaim(token, SecurityConstants.ACCOUNT);
            if (account == null) {
                throw new AuthenticationException("token无效!");
            }
            if (JwtUtil.verify(token)) {
                return new SimpleAuthenticationInfo(token, token, "customRealm");
            }
            throw new AuthenticationException("token已失效或不匹配.");
        }else {
            //一般不会进入这里
            throw new AuthenticationException("认证配置类异常.");
        }
    }
    /**
     * 重写密码匹配方法,添加每日登陆限制
     * @param token
     * @param info
     * @throws AuthenticationException
     */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        CredentialsMatcher cm = this.getCredentialsMatcher();
        if (cm != null) {
            if (!cm.doCredentialsMatch(token, info)) {
                String msg = "Submitted credentials for token [" + token + "] did not match the expected credentials.";
                throw new IncorrectCredentialsException(msg);
            }
        } else {
            throw new AuthenticationException("A CredentialsMatcher must be configured in order to verify credentials during authentication.  If you do not wish for credentials to be examined, you can configure an " + AllowAllCredentialsMatcher.class.getName() + " instance.");
        }
        limitLogin(token.getPrincipal().toString());
    }

    /**
     * 每日登陆限制，若超出限制则抛出异常
     * @param username ： 用户名
     * */
    private void limitLogin(String username){
        //keyPrefix：存入redis计数器key
        String keyPrefix = KeyPrefixConstants.LOGIN_COUNT+username;
        //limitPeriod：间隔多少秒进行访问
        int limitPeriod = DateTimeUtil.getSeconds();
        //获取存入redis的str
        String str = (String)redisTemplate.opsForValue().get(keyPrefix);
        //允许最大登陆的次数
        int limtTimeMax = DemoConstants.LIMIT_TIME_MAX;
        //redis存放时间与计数器map
        Map<String,Object> result = (Map<String,Object>) JSONUtil.parseObj(str);
        //获取当前时间
        String dateTime = DateUtil.offsetSecond(DateUtil.date(), limitPeriod).toString();
        //获取当前请求HttpServletRequest
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取机器ip
        String localip = RequestIpUtils.getIpAddr(request);
        //时间范围内第一次进入计数器重置
        Integer currentCount = 1;
        //查询当前计数
        Map map = new HashMap();
        map.put("openTime", dateTime);
        map.put("currentTime",new DateTime().toString());
        map.put("limitPeriod",limitPeriod);
        map.put("ip",localip);
        if (StrUtil.isEmpty(str)){
            map.put("currentCount", currentCount);
            redisTemplate.opsForValue().set(keyPrefix, JSONUtil.toJsonStr(map),limitPeriod, TimeUnit.SECONDS);
        }else {
            //查询当前计数++
            currentCount = (Integer)result.get("currentCount");
            currentCount++;
            map.put("currentCount", currentCount);
            if (currentCount > limtTimeMax){
                //当前解锁倒计时
                long between = DateUtil.between(DateUtil.parse( result.get("openTime").toString()), DateUtil.parse(new DateTime().toString()), DateUnit.SECOND);
                //转化为秒，一般倒计时以秒展现较为合适
                String formatBetween = DateUtil.formatBetween(between * 1000, BetweenFormatter.Level.SECOND);
                throw new ZKCustomException(StatusCode.FAILURE.getCode(),String.format("登入次数超出限制，上次登陆时间："+result.get("currentTime")+"，登陆ip："+result.get("ip")+"，距离解锁："+formatBetween));
            }else {
                redisTemplate.opsForValue().set(keyPrefix, JSONUtil.toJsonStr(map),limitPeriod, TimeUnit.SECONDS);
            }
        }
        log.info("用户正常登陆, 当前已登入{}次",currentCount);
    }
}