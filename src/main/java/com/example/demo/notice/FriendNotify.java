package com.example.demo.notice;

import com.example.demo.co.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  好友回调通知
 * </p>
 *
 * @author: 曾凯
 * @Version: V会.0
 * @since: 2020/12/11 14:40
 */
@Component
public class FriendNotify {

    @Autowired
    private UserService userService;

    public  String notifyTask(CallBack callBack, String userId){
        //查询当前用户
      //  User user = userService.qryUserById(userId);
        System.out.println(String.format("您的好友上线了!"));
        String s = callBack.FriendNotification(userId,userService);
        return s;
    }
}
