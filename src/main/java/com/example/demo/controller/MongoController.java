package com.example.demo.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.example.demo.annotation.LimitRequestAnnotation;
import com.example.demo.co.User;
import com.example.demo.constants.interfaces.DemoConstants;
import com.example.demo.annotation.Cacheable;
import com.example.demo.constants.enums.LimitTypeEnum;
import com.example.demo.notice.CallBackImpl;
import com.example.demo.notice.FriendNotify;
import com.example.demo.service.UserService;
import com.example.demo.util.Result;
import com.example.demo.util.SnowflakeIdWorkerUtil;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *      mongoTest
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/11/17 16:13
 */
@Slf4j
@RestController
@RequestMapping("/mongo")
@Api(tags = "mongodb测试")
public class MongoController {

    @Autowired
    private  MongoTemplate mongoTemplate;

    @Autowired
    UserService userService;


    /**
     * 保存
     * @param user ： 用户信息
     * @return 保存结果
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存", notes = "保存")
    public String save(@RequestBody  User user) {
        user.setId(SnowflakeIdWorkerUtil.getSnowId());
        System.out.println(JSONUtil.parse(user));
        mongoTemplate.save(user);
        return "保存成功";

    }

    /**
     * 分页查询用户信息
     * @return ： 查询结果
     */
    @GetMapping("/qryAll")
    @ApiOperation(value = "分页查询用户信息", notes = "分页查询用户信息")
    @LimitRequestAnnotation(value = "testLimit:qryAll:", exceptionMsg = "你的操作过于频繁,请休息一下吧!", key = "query", limitType = LimitTypeEnum.IP)
    @Cacheable(keyPrefix = DemoConstants.MongoQry_CACHE_KEY_PREFIX,expireTime = 5)
    public Result<List<User>> qryByName(){
        List<User> users = mongoTemplate.findAll(User.class);
        return Result.ok().setData(CollUtil.isEmpty(users) ? Collections.EMPTY_LIST : users);
    }

    /**
     * 根据用户ID发送通知
     * @param userId ： 用户ID
     * @return 发送结果
     */
    @GetMapping("/notifyTest/{userId}")
    @ApiOperation(value = "根据用户ID发送通知", notes = "根据用户ID发送通知")
    public String notifyTest(@PathVariable(value="userId")String userId){
        FriendNotify friendNotify = new FriendNotify();
        CallBackImpl callBack = new CallBackImpl(friendNotify);
        String s = callBack.FriendNotification(userId,userService);
        System.out.println("good");
        return s;
    }


    @GetMapping("/qryMap")
    @Cacheable(keyPrefix = DemoConstants.MongoQry_CACHE_KEY_PREFIX,fieldKey = "testMap",expireTime = 5,cacheEnable = true)
    public Map<String,List<User>> testMap(){
        Map<String, List<User>> integerListMap = new HashMap<>();
        List<User> userList = Lists.newArrayList();
        User user = new User();
        user.setId("1");
        userList.add(user);
        integerListMap.put("1",userList);
        integerListMap.put("2",userList);
        return integerListMap;
    }

    @GetMapping("/qryInteger")
    @Cacheable(keyPrefix = DemoConstants.MongoQry_CACHE_KEY_PREFIX,fieldKey = "qryInteger",expireTime = 5,cacheEnable = true)
    public Integer qryInteger(){
        Integer a = 88;
        return a;
    }

    @GetMapping("/qryString")
    @Cacheable(keyPrefix = DemoConstants.MongoQry_CACHE_KEY_PREFIX,fieldKey = "qryString",expireTime = 5,cacheEnable = true)
    public String qryString(){
        String a = "88";
        return a;
    }

    @GetMapping("/qryDouble")
    @Cacheable(keyPrefix = DemoConstants.MongoQry_CACHE_KEY_PREFIX,fieldKey = "qryDouble",expireTime = 5,cacheEnable = true)
    public Double qryDouble(){
        Double a = 88.888;
        return a;
    }

    @GetMapping("/qryLong")
    @Cacheable(keyPrefix = DemoConstants.MongoQry_CACHE_KEY_PREFIX,fieldKey = "qryLong",expireTime = 5,cacheEnable = true)
    public Long qryLong(){
        long a = 1111111111111111111L;
        return a;
    }
}
