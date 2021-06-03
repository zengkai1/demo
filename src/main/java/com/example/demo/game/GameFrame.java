package com.example.demo.game;

import javax.swing.*;
import java.awt.*;

/**
 * <p>
     游戏窗口
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月26日 15:33
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("test");//设置标题
        setSize(906, 655);//设定尺寸
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//点击关闭按钮是关闭程序
        setLocationRelativeTo(null);   //设置居中
        setResizable(false); //不允许修改界面大小
    }

    public static void main(String[] args) {
        GameFrame frame = new GameFrame();
        GamePanel panel = new GamePanel(frame);//创建实例
        frame.add(panel);//添加到窗体中
        frame.setVisible(true);//设定显示

    }
}
