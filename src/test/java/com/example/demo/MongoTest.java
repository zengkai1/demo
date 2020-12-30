package com.example.demo;

import com.example.demo.util.MD5SaltUtil;
import com.example.demo.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <p>
 *  mongo实体转化test
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/8 11:31
 */
@SpringBootTest
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

}

