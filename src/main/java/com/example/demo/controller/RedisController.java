package com.example.demo.controller;

import com.example.demo.redis.InstanceConfig;
import com.example.demo.redis.RedisLockUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *      Redis分布式锁测试
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/11/17 17:32
 */
@Slf4j
@RestController
@RequestMapping("/redis")
@Api(tags = "Redis分布式锁测试")
public class RedisController {
    @GetMapping(value = "/testRedis")
    public String testRedis() {
        String key = InstanceConfig.getInstanceId();
        RedisLockUtil.lock("testRedis:"+key);
        try {
            log.info("任务执行ing:{}");
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            log.info("任务异常:{}", e);
        } finally {
            RedisLockUtil.unlock("testRedis:"+key);
            log.info("释放锁testRedis-key:{}", key);
        }
        return "Task is OK";
    }


}
