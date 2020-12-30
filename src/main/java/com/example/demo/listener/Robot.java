package com.example.demo.listener;

import com.example.demo.listener.even.RobotEven;
import com.example.demo.listener.listener.RobotListener;

/**
 * <p>
 *   事件源:机器人
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 17:12
 */
public class Robot {

    private RobotListener listener;

    /**
     * 注册机器人监听器
     * @param listener
     */
    public void registerListener(RobotListener listener){
        this.listener = listener;
    }

    /**
     * 工作
     */
    public void working(){
        if(listener!=null){
            RobotEven even = new RobotEven(this);
            this.listener.working(even);
        }
        System.out.println("机器人开始工作......");
    }

    /**
     * 跳舞
     */
    public void dancing(){
        if(listener!=null){
            RobotEven even = new RobotEven(this);
            this.listener.dancing(even);
        }
        System.out.println("机器人开始跳舞......");
    }

}
