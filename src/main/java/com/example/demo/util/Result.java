package com.example.demo.util;

/**
 * <p>
 *
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/8 17:34
 */

import com.example.demo.constants.StatusCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * <p>Description: 数据对象包装类</p>
 *
 * @version 1.0
 * Copyright (c) 2019 贵州电商云
 * @Date 2019
 */
@ApiModel(value = "Result", description = "数据对象包装类")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "响应编码",example = "200")
    private Integer code;

    @ApiModelProperty(value = "响应信息")
    private String msg;

    @ApiModelProperty(value = "响应描述")
    private String description;

    @ApiModelProperty(value = "返回结果实体")
    private T data;

    public Result() { }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Result setDescription(String description) {
        this.description = description;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    public static Result ok() {
        return new Builder()
                .code(StatusCode.SUCCESS.getCode())
                .msg(StatusCode.SUCCESS.getMsg())
                .build();
    }

    public static Result failure() {
        return new Builder()
                .code(StatusCode.FAILURE.getCode())
                .msg(StatusCode.FAILURE.getMsg())
                .build();
    }


    public static Result error() {
        return new Builder()
                .code(StatusCode.ERROR.getCode())
                .msg(StatusCode.ERROR.getMsg())
                .build();
    }

    public static Result dup() {
        return new Builder()
                .code(StatusCode.DUP.getCode())
                .msg(StatusCode.DUP.getMsg())
                .build();
    }

    public static Result non() {
        return new Builder()
                .code(StatusCode.NON.getCode())
                .msg(StatusCode.NON.getMsg())
                .build();
    }

    /**
     * 操作成功时处理方法
     */
    public static Result handleSuccess(String msg) {
        Result result = ok();
        if (StringUtils.isNotBlank(msg)) {
            result.setMsg(msg);
        }
        return result;
    }

    /**
     * 操作成功时处理方法
     */
    public static Result handleSuccess(String msg, Object obj) {
        Result result = handleSuccess(msg);
        result.setData(obj);
        return result;
    }

    /**
     * 操作异常时处理方法
     *
     * @param e
     */
    public static Result handleError(Exception e) {
        e.printStackTrace();
        return error();
    }


    /**
     * 操作异常时处理方法
     */
    public static Result handleFailure(String msg) {
        Result result = failure();
        if (StringUtils.isNotBlank(msg)) {
            result.setMsg(msg);
        }
        return result;
    }

    /**
     * 操作异常时处理方法
     */
    public static Result handleNon(String msg) {
        Result result = non();
        if (StringUtils.isNotBlank(msg)) {
            result.setMsg(msg);
        }
        return result;
    }


    /**
     * 操作异常时处理方法
     */
    public static Result handleDup(String msg) {
        Result result = dup();
        if (StringUtils.isNotBlank(msg)) {
            result.setMsg(msg);
        }
        return result;
    }


    private Result(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.description = builder.description;
        this.data = (T) builder.data;
    }

    public static class Builder<T> {
        private Integer code;
        private String msg;
        private String description;
        private T data;

        public Builder code(Integer code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder data(T data) {
            this.data = data;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }
}
