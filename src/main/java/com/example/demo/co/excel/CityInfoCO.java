package com.example.demo.co.excel;

import com.example.demo.annotation.ExcelCondition;
import com.example.demo.aop.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  城市信息CO
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月20日 14:46
 */
@Data
@ApiModel(value = "CityInfoCO", description = "城市信息CO")
public class CityInfoCO implements Serializable {

    private static final long serialVersionUID = -592183458983052298L;

    @ApiModelProperty(value = "序号")
    @ExcelField(title = "序号", alias ="no", align = 2, sort = 0)
    private String no ;

    @ApiModelProperty(value = "城市")
    @ExcelField(title = "城市", alias = "city", align = 2, sort = 1)
    private String city;

    @ApiModelProperty(value = "区")
    @ExcelField(title = "区", alias = "county", align = 2, sort = 2)
    private String county;

    @ApiModelProperty(value = "代号")
    @ExcelField(title = "代号", alias = "code", align = 2, sort = 3)
    private String code;

}
