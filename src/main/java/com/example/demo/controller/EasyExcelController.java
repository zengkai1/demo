package com.example.demo.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.example.demo.co.excel.CityInfoCO;
import com.example.demo.config.excel.ExcelAnalysisHelper;
import com.example.demo.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 *  EasyExcel 相关
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月27日 11:06
 */
@Slf4j
@RestController
@RequestMapping("/easyExcel")
@Api(tags = "EasyExcel服务")
public class EasyExcelController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExcelAnalysisHelper analysisHelper;

    @PostMapping("/readExcel")
    @ApiOperation(value = "读取excel数据", notes = "读取excel数据")
    public Result readExcel(@ApiParam(value = "读取excel数据", required = true) MultipartFile file) throws IOException {
        List<CityInfoCO> cityInfoCOS = analysisHelper.getList(file, CityInfoCO.class,0,1);
        mongoTemplate.insertAll(cityInfoCOS);
        return Result.ok().setData(cityInfoCOS);
    }

    @GetMapping("/writeExcel")
    @ApiOperation(value = "写入excel数据", notes = "写入excel数据")
    public void writeExcel(HttpServletResponse response){
        try {
            List<CityInfoCO> cityInfoList = mongoTemplate.findAll(CityInfoCO.class);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("测试", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), CityInfoCO.class).sheet("模板").doWrite(cityInfoList);
        }catch (Exception e){
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
