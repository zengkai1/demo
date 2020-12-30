package com.example.demo.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 *   权限实体
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:05
 */
@Data
@AllArgsConstructor
@ApiModel(value = "Permissions", description = "权限实体")
public class Permissions {

    @ApiModelProperty(value = "权限ID")
    private String id;

    @ApiModelProperty(value = "权限名称")
    private String permissionsName;
}
