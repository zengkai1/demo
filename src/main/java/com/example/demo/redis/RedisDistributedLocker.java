package com.example.demo.redis;

import lombok.extern.log4j.Log4j2;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *      Redisson分布式锁封装接口方法实现类
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since
 */
@Component
@Log4j2
public class RedisDistributedLocker implements DistributedLocker{

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 使用分布式锁
     *
     * @param lockKey : redis分布式锁key
     * @return
     */
    @Override
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 使用分布式锁 设置过期时间
     *
     * @param lockKey : redis分布式锁key
     * @param timeout : 过期时间
     * @return
     */
    @Override
    public RLock lock(String lockKey, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 使用分布式锁,设置过期时间
     *
     * @param lockKey : redis 分布式锁key
     * @param unit    : 过期时间单位
     * @param timeout : 过期时间
     * @return
     */
    @Override
    public RLock lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout,unit);
        return lock;
    }

    /**
     * 尝试分布式锁,使用锁默认等待时间、超市时间。
     *
     * @param lockKey   ： redis分布式锁key
     * @param unit      ： 过期时间单位
     * @param waitTime  ： 获取锁最长等待时间
     * @param leaseTime ： 锁超时时间，超时后自动释放锁
     * @return
     */
    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime,leaseTime,unit);
        } catch (InterruptedException e) {
            log.error("尝试分布式锁失败:{}",e);
            return false;
        }
    }

    /**
     * 根据key释放锁
     *
     * @param lockKey ： redis分布式锁key
     */
    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 释放锁
     *
     * @param lock ： 锁
     */
    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }

    /**
     * 根据名称获取计数器
     *
     * @param name ： 名称
     * @return ： 返回计数器
     */
    @Override
    public RCountDownLatch getCountDownLatch(String name) {
        return redissonClient.getCountDownLatch(name);
    }

    /**
     * 根据名称获取信号量
     *
     * @param name ： 名称
     * @return ： 返回信号量
     */
    @Override
    public RSemaphore getSemaphore(String name) {
        return redissonClient.getSemaphore(name);
    }
}