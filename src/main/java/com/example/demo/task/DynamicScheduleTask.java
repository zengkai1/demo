package com.example.demo.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.demo.co.User;
import com.example.demo.notice.CallBack;
import com.example.demo.service.UserService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 *    动态：基于接口（SchedulingConfigurer）
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/7 17:29
 */
//@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class DynamicScheduleTask implements SchedulingConfigurer {

    @Value("${cron.cronTrigger}")
    private String cronTrigger;

    @Mapper
    public interface CronMapper {
        @Select("select cron from student.cron limit 1")
        public String getCron();
    }

    @Autowired      //注入mapper
    @SuppressWarnings("all")
    CronMapper cronMapper;

    /**
     * 执行定时任务
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(doTask(), getTrigger());
    }

    /**
     * 定时任务
     * @return
     */
    private Runnable doTask() {
        return new Runnable() {
            @Override
            public void run() {
                //添加任务内容(Runnable)
                System.out.println("执行动态定时任务: " + DateUtil.now());
            }
        };
    }


    /**
     * 触发器
     * @return
     */
    private Trigger getTrigger() {
        return new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                CronTrigger trigger = new CronTrigger(cronTrigger);
                return trigger.nextExecutionTime(triggerContext);
            }
        };
    }

}
