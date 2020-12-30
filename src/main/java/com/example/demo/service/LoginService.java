package com.example.demo.service;

import com.example.demo.co.LoginUser;
import com.example.demo.co.User;

/**
 * <p>
 *  登陆服务
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:07
 */
public interface LoginService {

    LoginUser getUserByName(String getMapByName);

}
