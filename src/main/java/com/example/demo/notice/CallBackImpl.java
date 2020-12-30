package com.example.demo.notice;

import cn.hutool.json.JSONUtil;
import com.example.demo.co.User;
import com.example.demo.service.UserService;
import com.example.demo.task.DynamicScheduleTask;
import com.example.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *  回调接口实现类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 9:53
 */
@Component
public class CallBackImpl implements CallBack {

    /**
     * 用户id
     */
    private String userId;

    private FriendNotify friendNotify;

    public CallBackImpl(FriendNotify friendNotify){
        this.friendNotify = friendNotify;
    }
    /**
     * 好友上线通知
     *
     * @param userId
     * @return
     */
    @Override
    public String FriendNotification(String userId,UserService userService ){
        setUserId(userId);
        //这里用一个线程就是异步，
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = userService.qryUserById(userId);
                System.out.println(String.format("用户信息为:%s", JSONUtil.parse(user)));
            }
        }).start();
        return "通知发送成功";
    }


    public void setUserId(String userId){
        this.userId = userId;
    }

}
