package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.annotation.Authorization;
import com.example.demo.co.LoginUser;
import com.example.demo.co.user.update.UpdateUserForm;
import com.example.demo.dto.user.LoginUserDTO;
import com.example.demo.dto.user.UserInfoDTO;
import com.example.demo.form.user.QueryUsersByPageForm;
import com.example.demo.service.UserService;
import com.example.demo.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 *  用户服务
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/16 10:23
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户服务")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户信息
     * @param queryUsersByPageForm ： 查询用户信息分页参数
     * @return ：用户分页信息
     */
    @GetMapping("/page")
    @Authorization(value = "user:page")
    @ApiOperation(value = "分页查询用户信息", notes = "分页查询用户信息")
    private Result<IPage<LoginUserDTO>> page(@Validated QueryUsersByPageForm queryUsersByPageForm){
       return Result.ok().setData(userService.qryUsersByPage(queryUsersByPageForm));
    }

    /**
     * 根据用户ID删除用户
     * @param id ： 用户id
     * @return ： 删除结果
     */
    @DeleteMapping("/del/{id}")
    @Authorization(value = "user:del")
    @ApiOperation(value = "根据用户ID删除用户", notes = "根据用户ID删除用户")
    private Result<String> delById(@PathVariable(value = "id") String id){
        if (userService.delById(id)){
            return Result.handleSuccess("删除成功");
        }
        return Result.handleFailure("删除失败");
    }

    /**
     * 修改用户信息
     * @param updateUserForm ： 用户提交表单
     * @return ： 修改结果
     */
    @PostMapping("/update")
    @Authorization(value = "user:update")
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    private Result<String> update(@RequestBody UpdateUserForm updateUserForm){
        //查询手机号是否重复
        LoginUser loginUser = userService.qryUserByPhone(updateUserForm.getPhone());
        if (Objects.nonNull(loginUser)){
            return Result.failure().setMsg("系统已存在该手机号，请重新输入");
        }
       if ( userService.update(updateUserForm)){
           return Result.handleSuccess("修改成功");
       }
       return Result.handleFailure("修改失败");
    }


    /**
     * 获取当前登陆用户信息
     * @return
     */
    @ApiOperation(value = "获取用户信息",notes = "刷新token")
    @Authorization(value = "user:getUserInfo")
    @GetMapping("/getUserInfo")
    public Result<UserInfoDTO> getUserInfo(){
        return userService.getUserInfo();
    }

}

