package com.example.demo.co.user.update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *      更新用户提交表单
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/28 10:26
 */
@Data
@ApiModel(value = "UpdateUserForm", description = "更新用户提交表单")
public class UpdateUserForm implements Serializable {

    private static final long serialVersionUID = 3956728919599620813L;

    @ApiModelProperty(value = "用户ID", required = true)
    @NotBlank(message = "用户ID不能为空")
    private String id;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

}
