package com.example.demo.config.swagger2;

import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.interceptor.CustomInterceptor;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  swagger配置
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/28 11:21
 */
@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "swagger")
@Data
@ComponentScan("com.example.demo.controller")
public class Swagger2Config{

    private String title;
    private String description;
    private String version;
    private String license;
    private String licenseUrl;
    private String basePackage;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String groupName;
    private String termsOfServiceUrl;

    @Bean
    public Docket createRestApi() {
        Parameter token = new ParameterBuilder().name(SecurityConstants.REQUEST_AUTH_HEADER)  //全局参数
                .description("用户登陆令牌")
                .parameterType("header")
                .modelRef(new ModelRef("String"))
                .required(false)
                .hidden(false)
                .build();
        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(token);
        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(parameters)
                //.pathMapping("/")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.demo.controller"))
                .paths(PathSelectors.any())
                .build()
                .groupName(groupName);
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .license(license)
                .licenseUrl(licenseUrl)
                .termsOfServiceUrl(termsOfServiceUrl)
                .contact(new Contact(contactName,contactUrl,contactEmail))
                .build();
    }

}
