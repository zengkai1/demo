package com.example.demo;

import java.util.Scanner;

/**
 * <p>
 *      核心代码-价值1个亿
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since:
 */
public class Robot {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str;
        while (true) {
            str = scanner.next();
            str = str.replace("吗", "");
            str = str.replace("谁", "小可爱");
            str = str.replace("你", "我");
            str = str.replace("？", "!");
            str = str.replace("?", "!");
            System.out.println(str);
        }
    }
}
