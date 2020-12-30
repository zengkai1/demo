package com.example.demo.co;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * <p>
 *      角色实体
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:02
 */
@Data
@AllArgsConstructor
@ApiModel(value = "Role", description = "角色实体")
public class Role {

    @ApiModelProperty(value = "角色ID")
    private String id;

    @ApiModelProperty(value = "角色名称")
    private String roleName;
    /**
     * 角色对应权限集合
     */
    @ApiModelProperty(value = "权限集合")
    private Set<Permissions> permissions;
}
