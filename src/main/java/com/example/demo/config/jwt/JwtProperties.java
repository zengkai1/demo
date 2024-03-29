package com.example.demo.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  jwt参数配置
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/30 14:23
 */
@ConfigurationProperties(prefix = "token")
@Component
@EnableConfigurationProperties({JwtProperties.class})
@Data
public class JwtProperties {

    /**
     * token过期时间，单位分钟
     */
    private Integer tokenExpireTime;

    /**
     * token加密密钥
     */
    private String secretKey;

    /**
     * 更新令牌时间，单位分钟
     */
    public Integer refreshCheckTime;

    /**
     * 验证码开关
     */
    public boolean verificationCode;

    /**
     * token校验启用开关
     */
    public boolean checkToken;
}
