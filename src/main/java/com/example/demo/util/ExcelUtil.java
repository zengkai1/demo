package com.example.demo.util;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.demo.config.excel.ExcelFileCellMergeStrategy;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 *  excel工具类(封装EasyExcel)
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年06月03日 17:47
 */
@Slf4j
@UtilityClass
public class ExcelUtil {

    /**
     * 写入Excel（合并单元格）
     *
     * @param response ：响应
     * @param dataList : 数据列表
     * @param fileName ： 文件名称
     * @param sheetName ： sheet名称
     * @param mergeColIndex ：需要合并的索引列，第一列为0，以此类推1,2,3...
     * @param mergeRowIndex ：需要从第*行开始，第一行为0，以此类推1,2,3...
     * @param <T> ： 数据类型
     * @param aClass : 模板类
     */
    public static<T> void writeExcel(HttpServletResponse response,List<T> dataList,String fileName,
                                     String sheetName,int[] mergeColIndex,int mergeRowIndex,Class<T> aClass){
        try {
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ExcelTypeEnum.XLSX);
            EasyExcel.write(response.getOutputStream(), aClass)
                    .sheet(sheetName)
                    .head(aClass)
                    .registerWriteHandler(new ExcelFileCellMergeStrategy(mergeColIndex,mergeRowIndex))
                    .doWrite(dataList);
        }catch (Exception e){
            //异常将msg、以及code返回给前端
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json; charset=utf-8");
            PrintWriter writer = null;
            try {
                writer = httpServletResponse.getWriter();
            } catch (IOException ioException) {
                log.error("写入异常信息失败：{}", ioException.getMessage());
            }
            Result result = Result.failure().setMsg(String.format("Excel导出失败：%s",e.getMessage()));
            writer.append(JSONUtil.toJsonStr(result));
            throw new RuntimeException("Excel导出异常!" + e.getMessage());
        }
    }

    /**
     * 写入Excel(无需合并单元格)
     *
     * @param response ：响应
     * @param dataList : 数据列表
     * @param fileName ： 文件名称
     * @param sheetName ： sheet名称
     * @param <T> ： 数据类型
     * @param aClass : 模板类
     */
    public static<T> void writeExcel(HttpServletResponse response,List<T> dataList,String fileName,
                                     String sheetName,Class<T> aClass){
        try {
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ExcelTypeEnum.XLSX);
            EasyExcel.write(response.getOutputStream(), aClass)
                    .sheet(sheetName)
                    .head(aClass)
                    .registerWriteHandler(new ExcelFileCellMergeStrategy())
                    .doWrite(dataList);
        }catch (Exception e){
            //异常将msg、以及code返回给前端
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json; charset=utf-8");
            PrintWriter writer = null;
            try {
                writer = httpServletResponse.getWriter();
            } catch (IOException ioException) {
                log.error("写入异常信息失败：{}", ioException.getMessage());
            }
            Result result = Result.failure().setMsg(String.format("Excel导出失败：%s",e.getMessage()));
            writer.append(JSONUtil.toJsonStr(result));
            throw new RuntimeException("Excel导出异常!" + e.getMessage());
        }
    }

}
