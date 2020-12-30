package com.example.demo;

/**
 * <p>
 * 飞驰而过的车，它的车牌是由一个四位数构成的，只有3个路人看到它
 *
 * 甲说：它的前两位是一样的
 *
 * 乙说：它的后两位是一样的，但是和它的前两位不一样
 *
 * 丙说：它是一个数的平方
 *
 * 请你根据路人甲乙丙的叙述，写一个程序，算出该车牌号
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/2 17:31
 */
public class CarNumber {
    public static void main(String[] args) {
        for(int i = 1000;i<=9999;i++){
            int first = i/1000;
            int second = i/100%10;
            int third =  i/10%10;
            int fourth = i%10;
            if (first==second && third==fourth){
                for (int j = 10;j<100;j++){
                    if (j*j==i){
                        System.out.println("车牌为："+i+", 这个数为: "+j);
                    }
                }
            }
        }
    }

}
