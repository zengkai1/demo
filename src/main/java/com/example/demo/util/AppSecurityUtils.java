package com.example.demo.util;

import cn.hutool.json.JSONUtil;
import com.example.demo.co.AppShiroUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * <p>
 *  获取Shiro token工具类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/14 15:05
 */
public class AppSecurityUtils {
    /**
     * 获取当前登录用户的用户名
     * @return
     */
    public static String obtainLoginedUsername() {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser == null || currentUser.getPrincipal() == null) {
            return "";
        }
        AppShiroUser shiroUser = (AppShiroUser)currentUser.getPrincipal();
        return shiroUser.getId();
    }

    /**
     * 获取当前用户token
     *
     * @return
     */
    public static String obtainAccessToken() {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser == null || currentUser.getPrincipal() == null) {
            return "";
        }
        AppShiroUser shiroUser = (AppShiroUser)currentUser.getPrincipal();
        return shiroUser.getAccessToken();
    }
}
