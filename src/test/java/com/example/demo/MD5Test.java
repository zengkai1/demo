package com.example.demo;

import com.example.demo.util.MD5SaltUtil;
import com.example.demo.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>
 *  MD5工具类测试
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 14:29
 */
@SpringBootTest
public class MD5Test {

    @Test
    void testMD5(){
        String password = "好的12345%$&*(+'.dwZ1";
        String password2 = "好的12345%$&*(+'.dwZ";
        System.out.println("-------------以下两种加密内容相同-------------");
        String encrypt = MD5Util.encrypt(password);
        String encrypt2 = MD5Util.encrypt(password);
        System.out.println("A加密结果："+encrypt);
        System.out.println("B加密结果："+encrypt2);
        System.out.println("A与B equals比较："+encrypt.equals(encrypt2));
        System.out.print("A与B ‘==’比较：");
        System.out.println(encrypt==(encrypt2));
        System.out.println("-------------以下两种加密内容不同-------------");
        String encrypt3 = MD5Util.encrypt(password);
        String encrypt4 = MD5Util.encrypt(password2);
        System.out.println("C加密结果："+encrypt3);
        System.out.println("D加密结果："+encrypt4);
        System.out.println("C与D equals比较："+encrypt3.equals(encrypt4));
        System.out.print("C与D ‘==’比较：");
        System.out.println(encrypt3==(encrypt4));
    }

    @Test
    void MD5SaltUtil(){
        System.out.println("-------------以下两种加密内容相同-------------");
        String password = "好的12345%$&*(+'.dwZ1";
        String password2 = "好的12345%$&*(+'.dwZ";
        String encrypt = MD5SaltUtil.encrypt(password);
        boolean verify = MD5SaltUtil.verify(password, encrypt);
        System.out.println("A比较结果："+ verify);
        System.out.println("A加密后字符串："+encrypt);

        String encrypt2 = MD5SaltUtil.encrypt(password);
        boolean verify2= MD5SaltUtil.verify(password, encrypt2);
        System.out.println("B比较结果："+verify2);
        System.out.println("B加密后字符串："+encrypt2);

        System.out.println("-------------以下两种加密内容不同-------------");
        String encrypt3 = MD5SaltUtil.encrypt(password);
        boolean verify3 = MD5SaltUtil.verify(password, encrypt);
        System.out.println("C比较结果："+ verify3);
        System.out.println("C加密后字符串："+encrypt3);

        String encrypt4 = MD5SaltUtil.encrypt(password2);
        boolean verify4 = MD5SaltUtil.verify(password, encrypt4);
        System.out.println("C比较结果："+ verify4);
        System.out.println("C加密后字符串："+encrypt4);
    }
}
