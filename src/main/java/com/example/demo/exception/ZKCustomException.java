package com.example.demo.exception;

/**
 * <p>
 *  个人自定义异常
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 10:18
 */
public class ZKCustomException extends RuntimeException{

    private Integer code;


    public  ZKCustomException(Integer code,String message){
        super(message);
        this.code = code;
    }

    public  ZKCustomException(String message){
        super(message);
    }

    public Integer getCode(){
        return code;
    }

    public void setCode(Integer code){
        this.code = code;
    }
}
