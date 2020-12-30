package com.example.demo.listener.even;

import com.example.demo.listener.Robot;

/**
 * <p>
 *  事件对象
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 17:17
 */
public class RobotEven {

    private Robot robot;

    public RobotEven(){
        super();
    }

    public RobotEven(Robot robot){
        super();
        this.robot = robot;
    }

    public Robot getRobot(){
        return robot;
    }

    public void setRobot(Robot robot){
        this.robot = robot;
    }
}
