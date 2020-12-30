package com.example.demo.service;

import java.io.Serializable;

/**
 * <p>
 *  支持序列化的Function
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/8 11:18
 */
@FunctionalInterface
public interface SFunction<T, R> extends Serializable {
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);
}
