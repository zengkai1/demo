package com.example.demo.form.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  查询用户分页信息表单
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/16 10:37
 */
@Data
@ApiModel(value = "QueryUsersByPageForm", description = "查询用户分页信息表单")
public class QueryUsersByPageForm implements Serializable {

    private static final long serialVersionUID = 2442244655506077190L;

    /**
     * 查询每页数目
     */
    @ApiModelProperty(value = "查询每页数目", required = true)
    private Integer limit = 10;

    /**
     * 查询页目
     */
    @ApiModelProperty(value = "查询页目", required = true)
    private Integer currentPage = 1;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;
}
