package com.example.demo.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.demo.annotation.ExcelCondition;
import com.example.demo.dto.user.UserExcelTemplate;
import com.example.demo.util.ExcelCheckUtils;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <p>
 *  Excel相关
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021/2/9 17:44
 */
@Slf4j
@RestController
@RequestMapping("/excel")
@Api(tags = "Excel服务")
public class ExcelController {

    @GetMapping("/getExcelTemplate")
    @ApiOperation(value = "获取excel模板", notes = "获取excel模板")
    public void getExcelTemplate(HttpServletRequest request, HttpServletResponse respons) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //反射获取excel模板类对象
        Class<UserExcelTemplate> clz = UserExcelTemplate.class;
        ///获取注解属性数组
        Field[] declaredFields = clz.getDeclaredFields();
        //将数组转为list
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(declaredFields));
        Map data = new HashMap();
        //迭代器
        Iterator<Field> iterator = fields.iterator();
        int count = 0;
        while(iterator.hasNext()){
            Field field = iterator.next();
            //在用反射时允许访问私有变量
            field.setAccessible(true);
            boolean annotationPresent = field.isAnnotationPresent(ExcelCondition.class);
            if (!annotationPresent){
                //移除其他字段，没被excelCondition标注的字段不做后续处理
                iterator.remove();
                continue;
            }
            ExcelCondition annotation = field.getAnnotation(ExcelCondition.class);
            //添加头部别名
            writer.addHeaderAlias(field.getName(),annotation.fieldName());
            data.put(field.getName(),annotation.example());
            writer.setColumnWidth(count,20);
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList();
            cellRangeAddressList.addCellRangeAddress(2,3,2,3);
            writer.addSelect(cellRangeAddressList,"男","女");
            count++;
        }
        //表头提示
        writer.merge(count-1,"批量导入用户数据");
        //设置行高度
        writer.setRowHeight(0,40);
        //设置内容字体
        Font font = writer.createFont();
        font.setBold(true);
        font.setColor(Font.COLOR_RED);

        //第二个参数表示是否忽略头部样式
        writer.getHeadCellStyle().setFont(font);

        List<Map> rows = Lists.newArrayList();
        rows.add(data);
        writer.write(rows,true);
        //返回文件名
        String fileName = ExcelCheckUtils.encodeFileName("批量导入用户数据模板", request);
        respons.setContentType("application/vnd.ms-excel;charset=utf-8");
        respons.setHeader("Content-Disposition","attachment;filename="+fileName);
        ServletOutputStream outputStream = respons.getOutputStream();
        writer.flush(outputStream);
        //关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(outputStream);

    }

}
