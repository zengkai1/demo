package com.example.demo.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.co.LoginUser;
import com.example.demo.co.User;
import com.example.demo.co.shiro.AppShiroUser;
import com.example.demo.co.shiro.UserContext;
import com.example.demo.co.user.update.UpdateUserForm;
import com.example.demo.constants.enums.DelFlagEnum;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.dto.user.LoginUserDTO;
import com.example.demo.dto.user.UserInfoDTO;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.form.user.QueryUsersByPageForm;
import com.example.demo.form.user.SaveUserForm;
import com.example.demo.service.UserService;
import com.example.demo.util.DateTimeUtil;
import com.example.demo.util.Result;
import com.example.demo.util.SnowflakeIdWorkerUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        user.setCreatetime(new Date());
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
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(StrUtil.isNotBlank(username),LoginUser::getUsername,username)
                .eq(LoginUser::getDelFlag, DelFlagEnum.NO.getCode());
        LoginUser one = this.getOne(queryWrapper);
        return one;
    }

    @Override
    public LoginUser qryUserByPhone(String phone) {
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(LoginUser::getPhone,phone)
                .eq(LoginUser::getDelFlag, DelFlagEnum.NO.getCode());
        return this.getOne(queryWrapper);
    }


    /**
     * 分页查询用户信息
     * @param queryUsersByPageForm ： 查询用户信息分页参数
     * @return ：用户分页信息
     */
    @Override
    public IPage<LoginUserDTO> qryUsersByPage(QueryUsersByPageForm queryUsersByPageForm) {
        IPage<LoginUser> userPage = new Page<>();
        userPage.setSize(queryUsersByPageForm.getLimit())
                .setCurrent(queryUsersByPageForm.getCurrentPage());
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().like(StrUtil.isNotBlank(queryUsersByPageForm.getUsername()), LoginUser::getUsername, queryUsersByPageForm.getUsername());
        userPage= this.page(userPage, queryWrapper);
        //转化为需要的dto对象
        IPage<LoginUserDTO> userDTOIPage = new Page<>();
        BeanUtils.copyProperties(userPage,userDTOIPage);
        //时间格式转化
        List<LoginUserDTO> loginUserDTOS = JSONUtil.toList(JSONUtil.parseArray(userDTOIPage.getRecords()), LoginUserDTO.class);
        loginUserDTOS.forEach(loginUserDTO -> {
            if (Objects.nonNull(loginUserDTO.getCreatetime())){
                String createtime = DateTimeUtil.dateStrConver(loginUserDTO.getCreatetime());
                loginUserDTO.setCreatetime(createtime);
            }
            if (Objects.nonNull(loginUserDTO.getUpdatetime())){
                String updatetime = DateTimeUtil.dateStrConver(loginUserDTO.getUpdatetime());
                loginUserDTO.setUpdatetime(updatetime);
            }
            if (Objects.nonNull(loginUserDTO.getPhone())){
                loginUserDTO.setPhone(hideTelephone(loginUserDTO.getPhone()));
            }
        });
        userDTOIPage.setRecords(loginUserDTOS);
        return userDTOIPage;
    }

    /**
     * 将手机号私密处理
     * @param telephone
     * @return
     */
    private  String hideTelephone(String telephone) {
        //方法1
/*        StringBuffer stringBuffer = new StringBuffer(telephone);
        StringBuffer replace = stringBuffer.replace(3, 7, "****");
        String hideTelephone = replace.toString();*/
        //方法2
        String hideTelephone = telephone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
        return hideTelephone;
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
        updateWrapper.lambda().eq(true,LoginUser::getUsername,updateUserForm.getUsername())
            .set(true,LoginUser::getPhone,updateUserForm.getPhone());
        return this.update(updateWrapper);
    }

    /**
     * 获取当前用户的信息
     *
     * @return 用户信息
     */
    @Override
    public Result<UserInfoDTO> getUserInfo() {
        //获取token信息
        AppShiroUser currentUser = UserContext.getCurrentUser();
        if (Objects.isNull(currentUser)){
            return Result.handleFailure("请先登录!");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        //设置用户属性
        BeanUtils.copyProperties(currentUser,userInfoDTO);
        return Result.handleSuccess("查询成功!",userInfoDTO);
    }

    /**
     * 根据邮箱查询用户信息
     *
     * @param email ： 邮箱
     * @return ： 登录用户信息
     */
    @Override
    public LoginUser qryUserByEmail(String email) {
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(true,LoginUser::getEmail,email);
        return this.baseMapper.selectOne(queryWrapper);
    }

    public static void main(String[] args) {
        String telephone = "";
        StringBuffer telephone1 =new StringBuffer("3");
        StringBuffer replace = telephone1.replace(3, 7, "****");
        System.out.println(replace);
        String hideTelephone = telephone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
        System.out.println(hideTelephone);
    }
}
