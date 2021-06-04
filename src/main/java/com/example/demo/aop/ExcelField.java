package com.example.demo.aop;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *  Excel注解定义
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月25日 14:10
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField  {

    /**
     * 导出字段标题（需要添加批注请用“**”分隔，标题**批注，仅对导出模板有效）
     */
    String title();

    /**
     * 别名
     */
    String alias() default "";

    /**
     * 字段类型（0：导出导入；1：仅导出；2：仅导入）
     */
    int type() default 0;

    /**
     * 导出字段对齐方式（0：自动；1：靠左；2：居中；3：靠右）
     *
     * 备注：Integer/Long类型设置居右对齐（align=3）
     */
    HorizontalAlignment align() default HorizontalAlignment.CENTER;

    /**
     * 导出字段字段排序（升序）
     */
    int sort() default 0;

    /**
     * 字段归属组（根据分组导出导入）
     */
    int[] groups() default {};

    /**
     * 格式类型：1-表头，2-栏目备注,3-说明
     */
    int formatType() default 1;

    /**
     * 高度
     */
    short hight() default 12;
}
