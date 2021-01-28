package com.example.demo;

import cn.hutool.json.JSONUtil;
import com.example.demo.co.LoginUser;
import com.example.demo.constants.StatusCode;
import com.example.demo.exception.ZKCustomException;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.util.*;

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

    @Test
    public void test1(){
        //订单重试列表
        HashSet<String> retryOrderSet = new HashSet<>();
        //订单失败记录Map
        Map<String,Integer> failOrderMap = new HashMap<>();

        List<LoginUser> loginUserList = Lists.newArrayList();
        LoginUser user = new LoginUser();
        user.setId("1");
        loginUserList.add(user);
        //记录下当前失败的订单id，并计数
        for (int i = 0;i<3;i++){
            loginUserList.forEach(loginUser  -> {
                if (Objects.isNull(failOrderMap.get(loginUser.getId()))){
                    failOrderMap.put(loginUser.getId(),0);
                }
                Integer count = failOrderMap.get(loginUser.getId());
                count ++;
                failOrderMap.replace(loginUser.getId(),count);
                if (count.intValue() >= 3 ){
                    retryOrderSet.add(loginUser.getId());
                }
                System.out.println(String.format("第%s次循环，set的内容是%s",count, JSONUtil.parse(retryOrderSet)));
            });
        }
    }

}
