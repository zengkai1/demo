package com.example.demo.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>
 *   权限认证注解
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since : 2020/12/8 15:10
 */
@Component
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authorization {

    String value() default "";
}
