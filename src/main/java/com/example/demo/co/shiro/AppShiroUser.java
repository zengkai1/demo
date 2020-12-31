package com.example.demo.co.shiro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 *  shiro用户验证实体
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/14 15:04
 */
@ApiModel(value = "AppShiroUser", description = "shiro用户验证实体")
public class AppShiroUser implements Serializable {

    private static final long serialVersionUID = -2320409490558933423L;

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "用户oauth授权accessToken")
    private String accessToken;

    @ApiModelProperty(value = "用户登录的机器IP")
    private String ipAddress;

    public AppShiroUser(String id, String accessToken,String ipAddress) {
        super();
        this.id = id;
        this.accessToken = accessToken;
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * 本函数输出将作为默认的<shiro:principal/>输出.
     */
    @Override
    public String toString() {
        return id;
    }
}
