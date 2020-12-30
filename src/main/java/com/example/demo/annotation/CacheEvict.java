package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *   REDIS缓存_销毁缓存key注解
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since : 2020/12/8 15:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheEvict {

    //缓存key前缀
    String keyPrefix();

    //动态key,若不传则只以key前缀作为全键key
    String fieldKey() default "";

}
