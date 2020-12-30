package com.example.demo.form.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
    @NotBlank(message = "用户名")
    private String username;

    @NotBlank(message = "密码")
    private String password;

    private Integer gender = 1;
}
