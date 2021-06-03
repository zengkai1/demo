package com.example.demo.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.demo.aop.ExcelField;
import com.example.demo.co.excel.CityInfoCO;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  解析Excel工具类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月24日 15:27
 */
@Slf4j
@UtilityClass
public class ParseExcelUtil {

    /**
     * 转换为List<T>
     * @param file 文件
     * @param beanType class类型
     * @return 转换后的数据LiSt
     */
    public static<T> List<T> toList(MultipartFile file ,  Class<T> beanType){
        ExcelReader reader = null;
        InputStream inputStream =  null;
        List<T> dataList = Lists.newArrayList();
        int startRowIndex = 1;
        int endRowIndex =  startRowIndex;
        int headerRowIndex = 0;
        try {
            inputStream = file.getInputStream();
            reader = ExcelUtil.getReader(file.getInputStream());
            //获取别名
            Map<String, String> headerAlias = getHeaderAilas(beanType);
            reader.setHeaderAlias(headerAlias);
            //计算最后一行
            endRowIndex = calculEndRow(reader);
            //dataList = reader.readAll(beanType);
            dataList = reader.read(headerRowIndex, startRowIndex, endRowIndex, beanType);
        } catch (Exception e) {
            log.error("解析excel文件异常：{}", e.getMessage());
        } finally {
            IoUtil.close(inputStream);
            reader.close();
        }
        return dataList;
    }

    /**
     * @desc 计算结束行
     * */
    private int calculEndRow(ExcelReader reader) {
        String endFlg = "end";
        int startRowIndex = 1;
        int endRowIndex = 0;
        List<List<Object>> rows =  reader.read(startRowIndex);
        for (List<Object> contents : rows) {
            endRowIndex ++;
            if (CollectionUtils.isEmpty(contents)) {
                continue;
            }
            if (contents.get(0) == null) {
                continue;
            }
            String fristCellValue = "";
            if (contents.get(0) instanceof String) {
                fristCellValue = (String) contents.get(0);
            }
            //查找到结束标识
            if (endFlg.equals(fristCellValue.toLowerCase())) {
                break;
            }
        }
        return endRowIndex;
    }

    /**
     * 属性名转化为HeaderAilas
     */
    public static Map<String, String> getHeaderAilas(Class<?> clazz) {
        Map<String, String> headerAliasMap = new HashedMap();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if (excelField == null) {
                continue;
            }
            headerAliasMap.put(excelField.title(),excelField.alias());
        }
        return headerAliasMap;
    }

    /**
     * 属性名转化为HeaderAilas
     */
    public static Map<String, String> getHeaderAilasHutool(Class<?> clazz) {
        Map<String, String> headerAliasMap = new LinkedHashMap();
        List<ExcelField> excelFields = getExcelFields(clazz);
        excelFields.forEach(excelField -> {
            headerAliasMap.put(excelField.alias(),excelField.title());
        });
        return headerAliasMap;
    }

    /**
     * 获取Excel表头
     */
    public static List<ExcelField> getExcelFields(Class<?> clazz){
        Field[] fields = clazz.getDeclaredFields();
        List<ExcelField> excelFields = Lists.newArrayList();
        for (Field field : fields) {
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if (excelField == null) {
                continue;
            }
            excelFields.add(excelField);
        }
        excelFields.sort((o1, o2) -> Integer.valueOf(o1.sort()).compareTo(Integer.valueOf(o2.sort())));
        return excelFields;
    }

    /**
     * 获取excel表头列表
     */
    public static List<String> getHeaderTitles(Class<?> clazz){
        List<ExcelField> excelFields = getExcelFields(clazz);
        List<String> titles = excelFields.stream().map(ExcelField::title).collect(Collectors.toList());
        return titles;
    }
}
