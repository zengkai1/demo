package com.example.demo.redis;

import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  Redisson分布式锁工具类
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since
 */
@Component
//@ConditionalOnWebApplication主要的用处是： 当Spring为web服务时，才使注解的类生效；通常是配置类；
//@ConditionalOnWebApplication
public class RedisLockUtil {

    @Autowired
    private RedisDistributedLocker locker;

    private static RedisDistributedLocker distributedLocker;

    @Bean
    public DistributedLocker locker(){
        return new RedisDistributedLocker();
    }

    /**
     * 初始化
     */
    @PostConstruct
    private void init(){
        distributedLocker = locker;
    }

    /**
     * 加锁
     */
    public static RLock lock(String lockKey){
        return distributedLocker.lock(lockKey);
    }

    /**
     * 释放锁
     * @param lockKey
     */
    public static void unlock(String lockKey){
        distributedLocker.unlock(lockKey);
    }

    /**
     * 带超时的锁
     * @param lockKey
     * @param timeout 超时时间  单位/秒
     * @return
     */
    public static RLock lock(String lockKey,int timeout){
        return distributedLocker.lock(lockKey,timeout);
    }

    /**
     * 带超时的锁
     * @param lockKey
     * @param timeout : 超时时间
     * @param unit : 时间单位
     * @return
     */
    public static RLock lock(String lockKey, int timeout, TimeUnit unit){
        return distributedLocker.lock(lockKey,unit,timeout);
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param waitTime : 最多等待时间
     * @param leaseTime : 上锁后自动释放锁时间
     * @return
     */
    public static boolean tryLock(String lockKey,int waitTime,int leaseTime){
        return distributedLocker.tryLock(lockKey,TimeUnit.SECONDS,waitTime,leaseTime);
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param unit : 时间单位
     * @param waitTime : 最多等待时间
     * @param leaseTime : 上锁后自动释放时间
     * @return
     */
    public static boolean tryLock(String lockKey,TimeUnit unit,int waitTime,int leaseTime){
        return distributedLocker.tryLock(lockKey,unit,waitTime,leaseTime);
    }
}