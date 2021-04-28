package com.example.demo.config.shiro;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  url配置
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年04月28日
 */
@Component
@ConfigurationProperties(prefix = "login-exclude-list")
@Data
public class ExcludeUrlPropertiesConfig {

    private List<String> excluded;

}
