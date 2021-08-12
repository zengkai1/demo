package com.example.demo;

import cn.hutool.json.JSONUtil;
import com.example.demo.util.MD5SaltUtil;
import com.example.demo.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.nio.file.OpenOption;
import java.util.*;

/**
 * <p>
 *  mongo实体转化test
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/8 11:31
 */
//@SpringBootTest
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void queryByName() {
        //Query query = new Query().addCriteria(Criteria.where(MongodbColumUtils.getEntityClum(User::getName)).is("kai"));
        //System.out.println(mongoTemplate.find(query, User.class));
    }

    @Test
    public void testJson(){
        int length = test.encrypt("1322469733734756353").length();
        System.out.println(length);
        System.out.println(test.encrypt("34756353"));
        System.out.println(test.encrypt("1322469733734756353"));
        System.out.println(test.encrypt("1322469733734756353"));
        System.out.println(MD5Util.encrypt("1322469733734756353"));
    }

    @Test
    public void test1(){
        Boolean aBoolean = null;
        if (aBoolean){
            System.out.println("不报错");
        }
    }

    @Test
    public void test2(){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplierId","1343849382564782082");
        paramMap.put("platformId","dsy_EMONpGJ9EijH");
        System.out.println(JSONUtil.parse(paramMap));
    }

    @Test
    public void test3(){
        String a = null;
        String s = Optional.ofNullable(a).orElse("ok");
        System.out.println(s);
    }

    @Test
    public void test4(){
        String a = "abc";
       // String s = Optional.ofNullable(a).orElse("ok");
       // System.out.println(s);
        String s1 = Optional.of(a).orElse("ok");
        System.out.println(s1);
    }
}

