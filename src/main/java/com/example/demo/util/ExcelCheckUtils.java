package com.example.demo.util;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *      文件名称中文乱码问题修复工具类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021/2/10 10:24
 */
@UtilityClass
public class ExcelCheckUtils {
    /**
     * https://blog.csdn.net/yyj108317/article/details/88793099
     *
     * @param fileName : 文件名
     * @param request  : HttpServletRequest
     * @Description: 导出文件转换文件名称编码
     */
    public String encodeFileName(String fileName, HttpServletRequest request) {
        String codedFilename = fileName;
        try {
            String agent = request.getHeader("USER-AGENT");
            // ie浏览器及Edge浏览器
            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent && -1 != agent.indexOf("Trident") || null != agent && -1 != agent.indexOf("Edge")) {
                String name = java.net.URLEncoder.encode(fileName, "UTF-8");
                codedFilename = name;
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {
                // 火狐,Chrome等浏览器
                codedFilename = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codedFilename + ".xlsx";
    }
}

