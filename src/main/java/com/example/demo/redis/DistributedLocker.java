package com.example.demo.redis;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *      Redisson分布式锁封装接口方法
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since
 */
public interface DistributedLocker {

    /**
     * 使用分布式锁
     *
     * @param lockKey : redis分布式锁key
     * @return
     */
    RLock lock(String lockKey);

    /**
     * 使用分布式锁 设置过期时间
     *
     * @param lockKey : redis分布式锁key
     * @param timeout : 过期时间
     * @return
     */
    RLock lock(String lockKey,int timeout);

    /**
     * 使用分布式锁,设置过期时间
     *
     * @param lockKey : redis 分布式锁key
     * @param unit : 过期时间单位
     * @param timeout : 过期时间
     * @return
     */
    RLock lock(String lockKey, TimeUnit unit , int timeout);

    /**
     * 尝试分布式锁,使用锁默认等待时间、超时时间。
     *
     * @param lockKey ： redis分布式锁key
     * @param unit ： 过期时间单位
     * @param waitTime ： 获取锁最长等待时间
     * @param leaseTime ： 锁超时时间，超时后自动释放锁
     * @return
     */
    boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime);

    /**
     * 根据key释放锁
     *
     * @param lockKey ： redis分布式锁key
     */
    void unlock(String lockKey);

    /**
     * 释放锁
     *
     * @param lock ： 锁
     */
    void unlock(RLock lock);

    /**
     * 根据名称获取计数器
     *
     * @param name ： 名称
     * @return ： 返回计数器
     */
    RCountDownLatch getCountDownLatch(String name);

    /**
     * 根据名称获取信号量
     *
     * @param name ： 名称
     * @return ： 返回信号量
     */
    RSemaphore getSemaphore(String name);
}