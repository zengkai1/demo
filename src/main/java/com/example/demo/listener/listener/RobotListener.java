package com.example.demo.listener.listener;

import com.example.demo.listener.even.RobotEven;

/**
 * <p>
 *  事件监听器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 17:15
 */
public interface RobotListener {

    public void working(RobotEven even);
    public void dancing(RobotEven even);

}
