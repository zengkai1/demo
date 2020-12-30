package com.example.demo;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * <p>
 *  字母连连看
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/2 9:54
 */
public class ZiMuLianLianKan {

    public static void main(String[] args) {
        System.out.println("请输入要进行消除的字符串");
        String scanner = new Scanner(System.in).next();
        long start1 = System.currentTimeMillis();
        gameFind1(scanner);
        long start2 = System.currentTimeMillis();
        System.out.println("gameFind1所需时间："+(start2-start1));
        long start3 = System.currentTimeMillis();
        gameFind2(scanner);
        long start4 = System.currentTimeMillis();
        System.out.println("gameFind2所需时间："+(start4-start3));
    }

    /**
     *     方法一      : 采用for循环进行遍历移除字母
     * @param scanner : 随机输入字符
     */
    public static void gameFind1(String scanner){
        StringBuffer finalResult = new StringBuffer();;
        while (true) {
            StringBuffer stringBuffer = new StringBuffer();
            List<String> charList = Lists.newArrayList();
            for (int i = 0 ; i < scanner.length() ; i ++){
                charList.add(String.valueOf(scanner.charAt(i)));
            }
            List<Integer> removeIndex = Lists.newArrayList();
            if (charList.size()==1){
                if (CollUtil.isNotEmpty(charList)){
                    finalResult = stringBuffer.append(charList.get(0));
                }
                break;
            }else if (charList.size()==2){
                if (charList.get(0).equals(charList.get(1))){
                    break;
                }
                finalResult = stringBuffer.append(charList.get(0)).append(charList.get(1));
                break;
            }else {
                for (int j = 0; j < charList.size()-1;j++){
                    for (int k = 0; k < charList.size()-1;k++){
                        if ((k+2 ) <= (charList.size()-1)){
                            if (charList.get(k).equals(charList.get(k+1)) && !charList.get(k).equals(charList.get(k+2))){
                                removeIndex.add(k);
                                removeIndex.add(k+1);
                            }
                        }
                    }
                }
            }
            if (charList.get(charList.size()-1).equals(charList.get(charList.size()-2))){
                removeIndex.add(charList.size()-1);
                removeIndex.add(charList.size()-2);
            }
            removeIndex = removeIndex.stream().distinct().collect(Collectors.toList());
            for (int l = 0 ; l <charList.size();l++ ){
                if (!removeIndex.contains(l)){
                    stringBuffer.append(charList.get(l));
                }
            }
            scanner = stringBuffer.toString();
            if (removeIndex.size()==0){
                finalResult = stringBuffer;
                break;
            }
        }
        if (finalResult.length()==0){
            System.out.println("YES");
        }else {
            System.out.println(finalResult);
        }
    }

    /**
     *      方法二     ：采用递归算法
     * @param scanner : 随机输入字符
     */
    public static void gameFind2(String scanner){
        List<String> charList = Lists.newArrayList();
        for (int i = 0 ; i < scanner.length() ; i ++){
            charList.add(String.valueOf(scanner.charAt(i)));
        }
        List<String> list = dealList(charList);
        StringBuffer finalResult = new StringBuffer();
        list.forEach(s -> {
            finalResult.append(s);
        });
        if (finalResult.length()==0){
            System.out.println("YES");
        }else {
            System.out.println(finalResult);
        }
    }

    /**
     * 递归处理列表
     * @param charList 字母列表
     * @return 处理后的字母列表
     */
    public static List<String> dealList( List<String> charList){
        for (int i=0;i<charList.size()-1;i++){
            if (charList.get(i).equals(charList.get(i+1))){
                charList.remove(i);
                charList.remove(i);
                dealList(charList);
            }
        }
        return charList;
    }

}
