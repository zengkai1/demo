package com.example.demo.exception;

import com.example.demo.constants.StatusCode;
import com.example.demo.util.Result;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;


/**
 * <p>
 *  全局异常处理器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 10:40
 */
@RestControllerAdvice
public class GlobalExceptionHandlerResolver extends ExceptionHandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerResolver.class);
    /**
     * 全局异常.
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(ZKCustomException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleGlobalException(ZKCustomException e) {
        logger.error("全局异常信息 exceptionMsg={} ,e={}", e.getMessage(), e);
        return Result.failure().setCode(e.getCode()==null?StatusCode.FAILURE.getCode():e.getCode()).setData(e.getLocalizedMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result errorHandler(AuthorizationException e) {
        logger.error("没有通过权限验证！", e);
        return Result.failure().setCode(StatusCode.FAILURE.getCode()).setMsg("您没有权限");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result  exceptionTranslation(Exception e) {
        return handleGlobalException(new ZKCustomException(e.getMessage()));
    }
}
