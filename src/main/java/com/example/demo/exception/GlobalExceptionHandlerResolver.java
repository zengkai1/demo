package com.example.demo.exception;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.demo.constants.StatusCode;
import com.example.demo.util.Result;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;


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
        logger.error("全局自定义异常拦截 exceptionMsg={} ,e={}", e.getMessage(), e);
        return Result.failure().setCode(e.getCode()==null?StatusCode.FAILURE.getCode():e.getCode()).setMsg(e.getLocalizedMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result errorHandler(AuthorizationException e) {
        logger.error("没有通过权限验证:{}！", e);
        return Result.failure().setCode(StatusCode.FAILURE.getCode()).setMsg("您没有权限");
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result incorrectCredentialsException(IncorrectCredentialsException e) {
        logger.error("密码不匹配:{}！", e);
        return Result.failure().setCode(StatusCode.FAILURE.getCode()).setMsg("用户名或密码错误");
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result authenticationException(UnauthenticatedException e) {
        logger.error("当前未登陆:{}！", e);
        return Result.failure().setCode(StatusCode.FAILURE.getCode()).setMsg("当前未登陆");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result  exceptionTranslation(Exception e) {
        logger.error("全局异常信息 exceptionMsg={} ,e={}", e.getMessage(), e);
        return Result.failure().setCode(StatusCode.FAILURE.getCode()).setMsg(e.getLocalizedMessage());
        //  return handleGlobalException(new ZKCustomException(e.getMessage()));
    }

    @ExceptionHandler(SignatureVerificationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result SignatureVerificationException(SignatureVerificationException e) {
        logger.error("TOKEN不匹配或已失效:{}！", e);
        return Result.failure().setCode(StatusCode.FAILURE.getCode()).setMsg("token不匹配或已失效！");
    }

}
