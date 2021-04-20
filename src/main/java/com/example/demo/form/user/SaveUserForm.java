package com.example.demo.form.user;

import com.example.demo.constants.interfaces.RegexConstants;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <p>
 *  保存用户提交表单
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/14 16:11
 */
@Data
public class SaveUserForm implements Serializable {

    private static final long serialVersionUID = 7132097150242862148L;

    @ApiModelProperty(value = "手机号",required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegexConstants.MOBILE_CHECK,message = RegexConstants.MOBILE_CHECK_MSG)
    private String phone;

    @ApiModelProperty(value = "username",required = true)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = RegexConstants.ACCOUNT_NAME,message = RegexConstants.ACCOUNT_NAME_MSG)
    private String username;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "性别 0：女 1：男",required = true)
    @Range(min = 0,max = 1,message = "性别范围：0-女 1-男")
    private Integer gender = 1;

    @ApiModelProperty(value = "邮箱",required = true)
    @Pattern(regexp = RegexConstants.EMAIL,message = RegexConstants.EMAIL_MSG)
    private String email;

    @ApiModelProperty(value = "邮箱验证码")
    @NotBlank(message = "邮箱验证码不能为空")
    private String code;
}
