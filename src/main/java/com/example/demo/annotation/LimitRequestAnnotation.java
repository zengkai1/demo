package com.example.demo.annotation;

/**
 * <p>
 *
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/9 17:12
 */

import com.example.demo.constants.enums.LimitTypeEnum;

import java.lang.annotation.*;

/**
 * <p>
 *  请求限流注解
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitRequestAnnotation {

    /**
     * 描述
     * @return : String
     */
    String value();

    /**
     * 异常信息
     * @return : String
     */
    String exceptionMsg();

    /**
     * 资源的名称
     * @return : String
     */
    String name() default "";

    /**
     * 资源的key
     * @return : String
     */
    String key() default "";

    /**
     * key的前缀 predix
     * @return : String
     */
    String prefix() default  "";

    /**
     * 给定的时间段 单位秒
     * @return : int
     */
    int period() default 30;

    /**
     * 最多的访问限制次数
     * @return : int
     */
    int count() default 5;

    /**
     * 类型
     * @return ：LimitTypeEnum（CUSTOMER：自定义key/IP : 根据请求IP）
     */
    LimitTypeEnum limitType() default LimitTypeEnum.CUSTOMER;
}