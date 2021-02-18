package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *  表格属性注解
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021/02/10 10:00
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCondition {

    boolean required() default true;

    String fieldName();

    String regex() default "";

    String format() default  "";

    String example() default "";
}
