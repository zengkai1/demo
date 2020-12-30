package com.example.demo.notice;

import com.example.demo.service.UserService;
import com.example.demo.util.Result;

/**
 * <p>
 *   回调接口
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 9:42
 */
public interface CallBack {

    /**
     * 好友上线通知
     * @param userId
     * @return
     */
    String FriendNotification(String userId, UserService userService);

}
