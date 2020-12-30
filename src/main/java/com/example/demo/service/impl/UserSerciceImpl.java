package com.example.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.co.LoginUser;
import com.example.demo.co.User;
import com.example.demo.co.user.update.UpdateUserForm;
import com.example.demo.constants.enums.DelFlagEnum;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.form.user.QueryUsersByPageForm;
import com.example.demo.form.user.SaveUserForm;
import com.example.demo.service.UserService;
import com.example.demo.util.SnowflakeIdWorkerUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *   用户服务实现类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 10:05
 */
@Service
public class UserSerciceImpl extends ServiceImpl<UserMapper, LoginUser > implements UserService {


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据用户ID查询用户信息
     *
     * @param id : 用户id
     * @return 用户信息
     */
    @Override
    public User qryUserById(String id) {
        return mongoTemplate.findById(id, User.class);
    }

    /**
     * 用户信息注册
     *
     * @param userForm ： 用户信息表单
     * @return ： true 注册成功 false 注册失败
     */
    @Override
    public boolean saveUser(SaveUserForm userForm) {
        LoginUser user = new LoginUser();
        BeanUtils.copyProperties(userForm,user);
        user.setId(SnowflakeIdWorkerUtil.getSnowId());
        return this.save(user);
    }

    /**
     * 根据用户名获取登陆用户信息
     *
     * @param username : 用户名
     * @return ： 登陆用户信息
     */
    @Override
    public LoginUser qryUserByUsername(String username) {
        LoginUser loginUser = new LoginUser();
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(StrUtil.isNotBlank(username),LoginUser::getUsername,username)
                .eq(LoginUser::getDelFlag, DelFlagEnum.NO.getCode());
        LoginUser one = this.getOne(queryWrapper);
        return one;
    }


    /**
     * 分页查询用户信息
     * @param queryUsersByPageForm ： 查询用户信息分页参数
     * @return ：用户分页信息
     */
    @Override
    public IPage<LoginUser> qryUsersByPage(QueryUsersByPageForm queryUsersByPageForm) {
        IPage<LoginUser> userPage = new Page<>();
        userPage.setSize(queryUsersByPageForm.getLimit())
                .setCurrent(queryUsersByPageForm.getCurrentPage());
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().like(StrUtil.isNotBlank(queryUsersByPageForm.getUsername()),LoginUser::getUsername,queryUsersByPageForm.getUsername());
        IPage<LoginUser> page = this.page(userPage, queryWrapper);
        return page;
    }

    /**
     * 根据用户ID删除用户
     *
     * @param id ： 用户id
     * @return ： 删除结果
     */
    @Override
    public boolean delById(String id) {
        LoginUser loginUser = this.baseMapper.selectById(id);
        if (Objects.isNull(loginUser)){
            throw new ZKCustomException("用户不存在");
        }
        return this.removeById(id);
    }

    /**
     * 修改用户信息
     *
     * @param updateUserForm ： 用户提交表单
     * @return ： 修改结果
     */
    @Override
    public boolean update(UpdateUserForm updateUserForm) {
        UpdateWrapper<LoginUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(true,LoginUser::getId,updateUserForm.getId())
            .set(StrUtil.isNotBlank(updateUserForm.getUsername()),LoginUser::getUsername,updateUserForm.getUsername());
        return this.update(updateWrapper);
    }


}
