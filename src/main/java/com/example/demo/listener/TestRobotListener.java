package com.example.demo.listener;

import com.example.demo.listener.listenerImpl.MyRobotListener;

/**
 * <p>
 *  测试事件监听
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 17:32
 */
public class TestRobotListener {

    public static void main(String[] args) {
        Robot robot = new Robot();
        robot.registerListener(new MyRobotListener());
        robot.working();
        robot.dancing();
    }
}
