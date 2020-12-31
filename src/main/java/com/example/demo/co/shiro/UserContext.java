package com.example.demo.co.shiro;

/**
 * <p>
 *  用户上下文
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/31 14:30
 */
//AutoCloseable自动关闭资源
public class UserContext implements AutoCloseable {

    static final ThreadLocal<AppShiroUser> current = new ThreadLocal<>();

    public UserContext(AppShiroUser user) {
        current.set(user);
    }

    public static AppShiroUser getCurrentUser() {
        return current.get();
    }

    @Override
    public void close() {
        current.remove();
    }
}
