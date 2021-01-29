package com.example.demo.constants.interfaces;

/**
 * <p>
 *  系统安全常量
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/30 14:07
 */
public interface SecurityConstants {

    String LOGIN_PREFIX = "demo:security:";

    //request请求头属性
    String REQUEST_AUTH_HEADER = "Authorization";

    //JWT-account
    String ACCOUNT = "account";

    //redis-key-前缀-shiro:refresh_token
    String PREFIX_SHIRO_REFRESH_TOKEN = LOGIN_PREFIX+"refresh_token:";

    //JWT-currentTimeMillis
    String CURRENT_TIME_MILLIS = "currentTimeMillis";

    String ACCESS_CONTROL_EXPOSE = "Access-Control-Expose-Headers";

    String TOKEN_PREFIX = "Bearer ";
}
