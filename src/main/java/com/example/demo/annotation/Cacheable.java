package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *      REDIS缓存_放入缓存key注解
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since : 2020/12/8 15:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Cacheable {

    //缓存key前缀，必传
    String keyPrefix();

    //动态key,若不传则只以key前缀作为全键key
    String fieldKey() default "";

    //key过期时间/分钟
    int expireTime() default 1;

    //Redis开关 true 开 false 关
    boolean cacheEnable() default true;

}