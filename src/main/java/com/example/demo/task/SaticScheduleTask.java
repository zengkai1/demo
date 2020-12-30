package com.example.demo.task;

import cn.hutool.core.date.DateUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


/**
 * <p>
 *  静态定时任务
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/7 16:56
 */
//@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class SaticScheduleTask  {

    //3.添加定时任务
    @Scheduled(cron = "0/10 * * * * ?")
    private void configureTasks() {
        System.err.println("执行静态定时任务时间: " + DateUtil.now());
    }

}
