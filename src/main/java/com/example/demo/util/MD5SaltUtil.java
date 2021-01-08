package com.example.demo.util;

import com.example.demo.constants.StatusCode;
import com.example.demo.exception.ZKCustomException;
import org.apache.shiro.codec.Hex;

import java.security.MessageDigest;
import java.util.Random;

/**
 * <p>
 *  MD5+盐加密/解密字符串
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 15:23
 */
public class MD5SaltUtil {

    /**
     * MD5+盐加密(每次加密同一字符串最终结果不一致)
     * @param source ：原字符串
     * @return ： 加密后的字符串
     */
    public static String encrypt(String source){
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer(16);
        stringBuffer.append(random.nextInt(99999999)).append(random.nextInt(99999999));
        int length = stringBuffer.length();
        if (length < 16){
            for (int i = 0 ; i < 16 - length; i++){
                stringBuffer.append("0");
            }
        }
        String salt = stringBuffer.toString();
        source = md5Hex(source + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48 ;i += 3){
            cs[i] = source.charAt(i/3*2);
            char c = salt.charAt(i/3);
            cs[i+1] = c;
            cs[i+2] = source.charAt(i/3*2+1);
        }
        return new String(cs);
    }

    /**
     * 校验加盐后是否和原文一致
     * @author daniel
     * @time 2016-6-11 下午8:45:39
     * @param password
     * @param md5
     * @return
     */
    public static boolean verify(String password, String md5) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        return md5Hex(password + salt).equals(new String(cs1));
    }
    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    private static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(Hex.encode(bs));
        } catch (Exception e) {
            throw new ZKCustomException(StatusCode.ERROR.getCode(),"加密异常");
        }
    }

}
