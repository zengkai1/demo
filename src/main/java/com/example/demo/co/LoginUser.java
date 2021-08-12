package com.example.demo.co;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

/**
 * <p>
 *  登陆用户实体类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("login_user")
@ApiModel(value = "LoginUser", description = "登陆用户实体类")
public class LoginUser implements Serializable{

    private static final long serialVersionUID = 2317937916019768545L;

    @ApiModelProperty(value = "用户id",hidden = true)
    private String id;

    @ApiModelProperty(value = "用户名",example = "zengkai123")
    private String username;

    @ApiModelProperty(value = "用户密码",example = "123456")
    private String password;

    @ApiModelProperty(value = "手机号",example = "18772100224")
    private String phone;

    @ApiModelProperty(value = "创建时间",hidden = true)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String createtime;

    @ApiModelProperty(value = "修改时间",hidden = true)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String updatetime;

    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    @TableLogic
    @ApiModelProperty(value = "删除标记",hidden = true,example = "0")
    private Integer delFlag = 0;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色列表",hidden = true)
    private Set<Role> roles;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "验证码不能为空！")
    @TableField(exist = false)
    private String code;

    @ApiModelProperty(value = "邮箱",example = "953459378@qq.com")
    private String email;
}
