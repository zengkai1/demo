package com.example.demo.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  用户实体类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/11/17 16:12
 */
@Data
@ApiModel(value = "User", description = "用户实体类")
public class User implements Serializable {

    private static final long serialVersionUID = 7474069332954807801L;

    @ApiModelProperty(value = "用户ID")
    private String id;

    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(value = "姓名")
    private String name;

    @NotBlank(message = "年龄不能为空" )
    @ApiModelProperty(value = "年龄",required = true,example = "0")
    private Integer age;

    @ApiModelProperty(value = "性别",example = "1")
    private Integer gender = 1;

}
