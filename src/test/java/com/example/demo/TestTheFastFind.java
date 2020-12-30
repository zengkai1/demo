package com.example.demo;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *    循环测试
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/1 17:39
 */
public class TestTheFastFind {
    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static AtomicInteger atomicInteger2 = new AtomicInteger(0);
    public static AtomicInteger atomicInteger3 = new AtomicInteger(0);
    public static AtomicInteger atomicInteger4 = new AtomicInteger(0);

    public static void main(String[] args) {
        testFastFind();
    }

    public static void testFastFind(){
        ArrayList<Object> futureList = Lists.newArrayList();
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(() -> forFind());
        futureList.add(voidCompletableFuture1);
        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(() ->  forFind2() );
        futureList.add(voidCompletableFuture2);
        CompletableFuture<Void> voidCompletableFuture3 = CompletableFuture.runAsync(() ->  forWhile() );
        futureList.add(voidCompletableFuture3);
        CompletableFuture<Void> voidCompletableFuture4 = CompletableFuture.runAsync(() ->  forWhile2() );
        futureList.add(voidCompletableFuture4);
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }

    public static void forFind(){
        long startTime = System.currentTimeMillis();
        for (;;){
            atomicInteger.incrementAndGet();
            if (atomicInteger.intValue()==1000000){
                System.out.println("forFind方法所需时间："+(System.currentTimeMillis() - startTime));
                break;
            }
        }
    }

    public static void forFind2(){
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++){
            atomicInteger2.incrementAndGet();
        }
        System.out.println("forFind2方法所需时间："+(System.currentTimeMillis() - startTime));
    }

    public static void forWhile(){
        long startTime = System.currentTimeMillis();
        while (true){
            atomicInteger3.incrementAndGet();
            if (atomicInteger3.intValue()==1000000){
                System.out.println("forWhile方法所需时间："+(System.currentTimeMillis() - startTime));
                break;
            }
        }
    }

    public static void forWhile2(){
        long startTime = System.currentTimeMillis();
        while (atomicInteger4.intValue()!=1000000){
            atomicInteger4.incrementAndGet();
        }
        System.out.println("forWhile2方法所需时间："+(System.currentTimeMillis() - startTime));
    }
}
