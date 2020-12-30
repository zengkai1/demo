package com.example.demo;

import com.example.demo.constants.StatusCode;
import com.example.demo.exception.ZKCustomException;

import java.security.MessageDigest;

/**
 * <p>
 *
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/17 15:28
 */
public class test {
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
        StringBuffer buffer = new StringBuffer(bytes.length / 2);
/*        for (int i = 0 ; i < bytes.length ; i++){
            if (((int) bytes[i] & 0xff) < 0x10){
                buffer.append("0");
            }
            buffer.append(Long.toString((int) bytes[i] & 0xff, 16));
        }*/
        return buffer.toString().substring(24);
    }

}
