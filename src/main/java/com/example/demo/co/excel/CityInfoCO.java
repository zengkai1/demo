package com.example.demo.co.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
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
@HeadFontStyle(bold = true,color = -2)//表头格式
@HeadRowHeight(30)//表头高度
@ColumnWidth(20)//表头宽度
@ContentRowHeight(value = 18)//本文高度
@ApiModel(value = "CityInfoCO", description = "城市信息CO")
public class CityInfoCO implements Serializable {

    private static final long serialVersionUID = -592183458983052298L;

    @ApiModelProperty(value = "序号")
    @ExcelField(title = "序号", alias ="no",  sort = 0)
    @ExcelProperty(value = "序号",index = 0)
    //@ColumnWidth(50) 宽度会覆盖上面
    private String no ;

    @ApiModelProperty(value = "城市")
    @ExcelProperty(value = "城市",index = 1)
    @ExcelField(title = "城市", alias = "city",  sort = 1)
    private String city;

    @ApiModelProperty(value = "区")
    @ExcelProperty(value = "区",index = 2)
    @ExcelField(title = "区", alias = "county",  sort = 2)
    private String county;

    @ApiModelProperty(value = "代号")
    @ExcelProperty(value = "代号",index = 3)
    @ExcelField(title = "代号", alias = "code",  sort = 3)
    private String code;

}
