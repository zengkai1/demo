package com.example.demo.exception;

import com.example.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>
 *  默认异常转换类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/28 15:08
 */
/*
@Order(0)
@ControllerAdvice
public class DefaultExceptionHandler {

    @Autowired
    private GlobalExceptionHandlerResolver globalExceptionHandlerResolver ;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionTranslation(Exception e) {
        return globalExceptionHandlerResolver.handleGlobalException(new ZKCustomException(e.getMessage()));
    }
}
*/
