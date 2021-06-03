package com.example.demo.config.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.util.CollectionUtils;
import com.example.demo.aop.ExcelField;
import com.example.demo.constants.StatusCode;
import com.example.demo.exception.ZKCustomException;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * <p>
 *  ExcelAnalysisHelper
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月27日 15:59
 */
@Component
public class ExcelAnalysisHelper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelAnalysisHelper.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<T> getList(MultipartFile file, Class<T> clazz) {
        return getList(file, clazz, 0, 1);
    }

    public List<T> getList(MultipartFile file, Class<T> clazz, Integer sheetNo, Integer headRowNumber) {
        ExcelDataListener<T> listener = new ExcelDataListener<T>(mongoTemplate,headRowNumber);
        try {
            EasyExcel.read(file.getInputStream(), clazz, listener).extraRead(CellExtraTypeEnum.MERGE).sheet(sheetNo).headRowNumber(headRowNumber).doRead();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
        if (CollectionUtils.isEmpty(extraMergeInfoList)) {
            return listener.getData();
        }
        List<T> data = explainMergeData(listener.getData(), extraMergeInfoList, headRowNumber);
        return data;
    }

    /**
     * 处理合并单元格
     *
     * @param data               解析数据
     * @param extraMergeInfoList 合并单元格信息
     * @param headRowNumber      起始行
     * @return 填充好的解析数据
     */
    private List<T> explainMergeData(List<T> data, List<CellExtra> extraMergeInfoList, Integer headRowNumber) {
        //循环所有合并单元格信息
        extraMergeInfoList.forEach(cellExtra -> {
            int firstRowIndex = cellExtra.getFirstRowIndex() - headRowNumber;
            int lastRowIndex = cellExtra.getLastRowIndex() - headRowNumber;
            int firstColumnIndex = cellExtra.getFirstColumnIndex();
            int lastColumnIndex = cellExtra.getLastColumnIndex();
            //获取初始值
            Object initValue = getInitValueFromList(firstRowIndex, firstColumnIndex, data);
            //设置值
            for (int i = firstRowIndex; i <= lastRowIndex; i++) {
                for (int j = firstColumnIndex; j <= lastColumnIndex; j++) {
                    setInitValueToList(initValue, i, j, data);
                }
            }
        });
        return data;
    }

    /**
     * 设置合并单元格的值
     *
     * @param filedValue  值
     * @param rowIndex    行
     * @param columnIndex 列
     * @param data        解析数据
     */
    public void setInitValueToList(Object filedValue, Integer rowIndex, Integer columnIndex, List<T> data) {
        T object = data.get(rowIndex);
        for (Field field : object.getClass().getDeclaredFields()) {
            //提升反射性能，关闭安全检查
            field.setAccessible(true);
            ExcelField annotation = field.getAnnotation(ExcelField.class);
            if (annotation != null) {
                if (annotation.sort() == columnIndex) {
                    try {
                        field.set(object, filedValue);
                        break;
                    } catch (IllegalAccessException e) {
                        throw new ZKCustomException(StatusCode.FAILURE.getCode(), "解析数据时发生异常!");
                    }
                }
            }
        }
    }


    /**
     * 获取合并单元格的初始值
     * rowIndex对应list的索引
     * columnIndex对应实体内的字段
     *
     * @param firstRowIndex    起始行
     * @param firstColumnIndex 起始列
     * @param data             列数据
     * @return 初始值
     */
    private Object getInitValueFromList(Integer firstRowIndex, Integer firstColumnIndex, List<T> data) {
        Object filedValue = null;
        T object = data.get(firstRowIndex);
        for (Field field : object.getClass().getDeclaredFields()) {
            //提升反射性能，关闭安全检查
            field.setAccessible(true);
            ExcelField annotation = field.getAnnotation(ExcelField.class);
            if (annotation != null) {
                if (annotation.sort() == firstColumnIndex) {
                    try {
                        filedValue = field.get(object);
                        break;
                    } catch (IllegalAccessException e) {
                        throw new ZKCustomException(StatusCode.FAILURE.getCode(), "解析数据时发生异常!");
                    }
                }
            }
        }
        return filedValue;
    }
}
