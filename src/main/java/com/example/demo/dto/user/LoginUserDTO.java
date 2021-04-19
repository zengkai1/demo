package com.example.demo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * <p>
 *  用户信息实体类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since:
 */
@Data
@ApiModel(value = "LoginUserDTO", description = "登陆用户实体类")
public class LoginUserDTO implements Serializable {

    private static final long serialVersionUID = -7688858969607654785L;

    @ApiModelProperty(value = "用户id",hidden = true)
    private String id;

    @ApiModelProperty(value = "用户名",example = "zengkai123")
    private String username;

    @ApiModelProperty(value = "用户密码",example = "123456")
    private String password;

    @ApiModelProperty(value = "手机号",example = "18772100224")
    private String phone;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createtime;

    @ApiModelProperty(value = "修改时间",hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatetime;
}
