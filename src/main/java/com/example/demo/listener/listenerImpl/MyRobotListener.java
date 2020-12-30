package com.example.demo.listener.listenerImpl;

import com.example.demo.listener.Robot;
import com.example.demo.listener.even.RobotEven;
import com.example.demo.listener.listener.RobotListener;

/**
 * <p>
 *  实现事件监听器MyRobotListener：
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 17:27
 */
public class MyRobotListener implements RobotListener {

    @Override
    public void working(RobotEven even) {
        Robot robot = even.getRobot();
        System.out.println("收到工作指令");
    }

    @Override
    public void dancing(RobotEven even) {
        Robot robot = even.getRobot();
        System.out.println("收到跳舞指令");

    }
}
