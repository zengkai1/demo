package com.example.demo.service;

import cn.hutool.core.lang.Console;
import cn.hutool.poi.excel.sax.handler.RowHandler;

import java.util.List;

/**
 * <p>
 *   RowHandler接口，这个接口是Sax读取的核心，通过实现handle方法编写我们要对每行数据的操作方式
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月24日 14:35
 */
public class MyRowHandler implements RowHandler {
    @Override
    public void handle(int sheetIndex, long rowIndex, List<Object> rowlist) {
        Console.log("[{}] [{}] {}", sheetIndex, rowIndex, rowlist);
    }
}
