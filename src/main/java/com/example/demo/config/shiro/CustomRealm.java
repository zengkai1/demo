package com.example.demo.config.shiro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.demo.co.LoginUser;
import com.example.demo.co.Permissions;
import com.example.demo.co.Role;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Objects;

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
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    public  CustomRealm( ){
        setAuthenticationTokenClass(CustomToken.class);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomToken;
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
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)  {

        String token = (String)authenticationToken.getPrincipal();
        String account  = JwtUtil.getClaim(token, SecurityConstants.ACCOUNT);
        if (account == null) {
            throw new AuthenticationException("token invalid");
        }
        LoginUser user = userService.qryUserByUsername(account);
        if (user == null) {
            throw new AuthenticationException("user didn't existed!");
        }
        String refreshTokenCacheKey = SecurityConstants.PREFIX_SHIRO_REFRESH_TOKEN + account;
        String currentTimeMillisRedis = (String)redisTemplate.opsForValue().get(refreshTokenCacheKey);
        if (StrUtil.isNotEmpty(currentTimeMillisRedis)) {
            // 获取AccessToken时间戳，与RefreshToken的时间戳对比
            if (JwtUtil.getClaim(token, SecurityConstants.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
                SimpleAuthenticationInfo shiroRealm = new SimpleAuthenticationInfo(token, token, "customRealm");
                return shiroRealm;
            }
        }
        throw new AuthenticationException("Token expired or incorrect.");
    }
}
