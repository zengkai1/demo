package com.example.demo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 *  用户信息DTO
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021/1/29 16:16
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "User", description = "用户实体类")
public class UserInfoDTO {

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "用户oauth授权accessToken")
    private String accessToken;

    @ApiModelProperty(value = "用户登录的机器IP")
    private String ipAddress;

}
