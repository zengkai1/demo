package com.example.demo.util;

import com.example.demo.constants.StatusCode;
import com.example.demo.exception.ZKCustomException;

import java.security.MessageDigest;

/**
 * <p>
 *   MD5加密类（封装jdk自带的md5加密方法）
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 14:21
 */
public class MD5Util {

    /**
     * 普通的加密方法(每次加密同一字符串最终结果一致)
     * @param source ：原字符串
     * @return ： 加密后的字符串
     */
    public static String encrypt(String source){
        return encodeMd5(source.getBytes());
    }

    private static String encodeMd5(byte[] source) {
        try{
            return encodeHex(MessageDigest.getInstance("MD5").digest(source));
        }catch (Exception e){
            throw new ZKCustomException(StatusCode.ERROR.getCode(),"加密异常");
        }
    }

    private static String encodeHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);
        for (int i = 0 ; i < bytes.length ; i++){
            if (((int) bytes[i] & 0xff) < 0x10){
                buffer.append("0");
            }
            buffer.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buffer.toString();
    }

}
