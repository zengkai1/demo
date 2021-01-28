package com.example.demo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.co.LoginUser;
import com.example.demo.co.User;
import com.example.demo.co.user.update.UpdateUserForm;
import com.example.demo.form.user.QueryUsersByPageForm;
import com.example.demo.form.user.SaveUserForm;
import com.example.demo.util.Result;

/**
 * <p>
 *  用户信息接口
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 10:00
 */
public interface UserService{

    /**
     * 根据用户ID查询用户信息
     * @param id : 用户id
     * @return 用户信息
     */
    User qryUserById(String id);

    /**
     * 用户信息注册
     * @param userForm ： 用户信息表单
     * @return ： true 注册成功 false 注册失败
     */
    boolean saveUser(SaveUserForm userForm);

    /**
     *
     * 根据用户名获取登陆用户信息
     * @param username : 用户名
     * @return : 登陆用户信息
     */
    LoginUser qryUserByUsername(String username);

    /**
     * 分页查询用户信息
     * @param queryUsersByPageForm ： 查询用户信息分页参数
     * @return ：用户分页信息
     */
    IPage<LoginUser> qryUsersByPage(QueryUsersByPageForm queryUsersByPageForm);

    /**
     * 根据用户ID删除用户
     * @param id ： 用户id
     * @return ： 删除结果
     */
    boolean delById(String id);

    /**
     * 修改用户信息
     * @param updateUserForm ： 用户提交表单
     * @return ： 修改结果
     */
    boolean update(UpdateUserForm updateUserForm);

}
