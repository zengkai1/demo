package com.example.demo.controller;

import com.example.demo.co.LoginUser;
import com.example.demo.constants.interfaces.KeyPrefixConstants;
import com.example.demo.form.user.SaveUserForm;
import com.example.demo.service.UserService;
import com.example.demo.util.AppSecurityUtils;
import com.example.demo.util.MD5SaltUtil;
import com.example.demo.util.Result;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 *   登陆服务
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 16:47
 */
@Slf4j
@RestController
@Api(tags = "登陆服务")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户登入
     * @param user：登陆的用户信息
     * @return ： 登陆结果
     */
    @GetMapping("/login")
    @ApiOperation(value = "用户登入", notes = "用户登入")
    public Result<String> login( LoginUser user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            return Result.error().setMsg("请输入用户名和密码登入系统");
        }
        //用户认证信息
        Subject subject = SecurityUtils.getSubject();
        //永不过期
        subject.getSession().setTimeout(-1000L);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                user.getUsername(),
                user.getPassword()
        );
        //进行验证
        subject.login(usernamePasswordToken);
/*        subject.checkRole("admin");
        subject.checkPermissions("query", "add");*/
        return Result.ok()/*.setData("当前token:"+token)*/.setMsg("登陆成功");
    }

    /**
     * 用户注册
     * @param userForm ： 用户提交表单
     * @return ： 注册结果
     */
    @RequestMapping(value = "/register" ,method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public Result<String> register(@Validated @RequestBody SaveUserForm userForm){
        //查询用户名是否重复
        LoginUser user = userService.qryUserByUsername(userForm.getUsername());
        if (Objects.nonNull(user)){
            return Result.failure().setMsg("系统已存在该账号，请重新输入");
        }
        //密码加密
        userForm.setPassword(MD5SaltUtil.encrypt(userForm.getPassword()));
        //入库
        boolean saveUser = userService.saveUser(userForm);
        if (saveUser){
            return Result.ok().setMsg("注册成功");
        }
        return Result.failure().setMsg("注册失败");
    }

    /**
     * 用户登出
     * @return 登出结果
     */
    @RequiresAuthentication
    @ApiOperation(value = "用户登出", notes = "用户登出")
    @GetMapping(value = "/logout")
    public Result<String> logout() {
        //在这里执行退出系统前需要清空的数据
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()) {
            subject.logout();
        }
        System.out.println("退出登录成功");
        return Result.ok().setMsg("退出登录");
    }

    /**
     * 通过用户名解除用户限制
     * @param username ： 用户名
     * @return ： 结果
     */
    @ApiOperation(value = "通过用户名解除用户限制", notes = "通过用户名解除用户限制")
    @GetMapping("/unfreezeByUsername/{username}")
    public Result<String> unfreezeByUsername(@ApiParam(name = "username", value = "用户名",example = "zengkai") @PathVariable("username") String username){
        LoginUser loginUser = userService.qryUserByUsername(username);
        if (Objects.isNull(loginUser)){
            return Result.handleFailure("无此用户");
        }
        //keyPrefix：存入redis计数器key
        String keyPrefix = KeyPrefixConstants.LOGIN_COUNT+username;
        if (redisTemplate.delete(keyPrefix)){
            return Result.handleSuccess("解除成功");
        }
        return Result.handleFailure("解除失败，该用户未被锁定");
    }

    /**
     * 错误页面
     * @return 错误页面
     */
    @ApiOperation(value = "错误页面", notes = "错误页面")
    @GetMapping("/error")
    public Result<String> err(){
        return Result.failure().setMsg("哎哟，爆炸了唷！");
    }
}
