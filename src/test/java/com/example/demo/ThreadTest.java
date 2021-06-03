package com.example.demo;

import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * <p>
 *      并发测试
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月26日 15:54
 */
public class ThreadTest {

    public static void main(String[] args) {
        ConcurrencyTester tester = ThreadUtil.concurrencyTest(100, () -> {
            // 测试的逻辑内容
            long delay = RandomUtil.randomLong(100, 1000);
            ThreadUtil.sleep(delay);
            Console.log("{} test finished, delay: {}", Thread.currentThread().getName(), delay);
        });
        // 获取总的执行时间，单位毫秒
        Console.log(tester.getInterval());
    }
}
