package com.example.demo;

import cn.hutool.json.JSONUtil;
import com.example.demo.redis.InstanceConfig;
import com.example.demo.redis.RedisLockUtil;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootTest
class DemoApplicationTests {

    private AtomicInteger atomicInteger = new AtomicInteger(1000);
    public static long now_time;
    public static long seed;
    public static int[] get_red_packets(int money, int num) {
        Random random = new Random(seed);
        seed = random.nextLong();
        int[] res = new int[num];
        double[] temp=new double[num];
        double sum = 0;
        int sum2 = 0;
        for (int i = 0; i < num; i++) {
            temp[i] = random.nextDouble();
            sum += temp[i];
        }
        for (int i = 0; i < num; i++) {
            res[i] = 1+ (int)(temp[i] / sum * (money-num));
            sum2 += res[i]-1;
        }
        res[random.nextInt(num)]+=money-sum2-num;
        return res;
    }

    public static void show(int[] red_packet){
        ArrayList<BigDecimal> bigDecimals = Lists.newArrayList();
        for (int rp : red_packet) {
            BigDecimal bigDecimal = new BigDecimal(rp).setScale(2);
            bigDecimal.add(bigDecimal);
        }
        System.out.println("红包 : " + JSONUtil.parse(bigDecimals));
    }

    public static void main(String[] args) {
        int num, money;
        Scanner scanner = new Scanner(System.in);
        now_time = System.currentTimeMillis();
        Random init_random = new Random(now_time);
        seed = init_random.nextLong();
        System.out.println("请输入要分发的红包数量:");
        num = scanner.nextInt();
        System.out.println("请输入要分发的红包总金额(分):");
        money = scanner.nextInt();
        int a[] = get_red_packets(money,num);
        show(a);
    }
    @Test
    void contextLoads() {

    }

    public void getCards(){
        String key = InstanceConfig.getInstanceId();
        RedisLockUtil.lock("testRedis:"+key);
    }

    @Test
    void test1() throws ExecutionException, InterruptedException {

        ArrayList<CompletableFuture> arrayList = Lists.newArrayList();
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
                atomicInteger.incrementAndGet();
                System.out.println("执行任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        CompletableFuture future2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                atomicInteger.incrementAndGet();
                System.out.println("执行任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        arrayList.add(future);
        arrayList.add(future2);
        future.get();
        System.out.println(    atomicInteger.intValue());
    }
}
