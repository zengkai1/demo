package com.example.demo.dto.user;

import com.example.demo.annotation.ExcelCondition;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 *  用户excel映射
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021/2/10 9:44
 */
public class UserExcelTemplate {

    /**
     * 用户名 : 必须包含字母和数字，6-18位
     */
    @ExcelCondition(fieldName = "用户名",regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$",example = "zengkai")
    private String username;

    /**
     * 密码 ： 必须包含字母和数字，6-18位
     */
    @ExcelCondition(fieldName = "密码",regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$", example = "123456")
    private String password;

    /**
     * 手机号码
     */
    @ExcelCondition(fieldName = "手机号码",regex = "^(((13[0-9]{1})|(15[0-9]{1})|(16[0-9]{1})|(17[3-8]{1})|(18[0-9]{1})|(19[0-9]{1})|(14[5-7]{1}))+\\d{8})$", example = "18772100224")
    private String phone;

    /**
     * 性别
     */
    @ExcelCondition(fieldName = "性别",regex = "^(男|女)$",example = "男")
    private String gender;
}
