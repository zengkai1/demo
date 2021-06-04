package com.example.demo.controller;

import com.example.demo.co.excel.CityInfoCO;
import com.example.demo.config.excel.ExcelAnalysisHelper;
import com.example.demo.util.ExcelUtil;
import com.example.demo.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
        List<CityInfoCO> cityInfoList = mongoTemplate.findAll(CityInfoCO.class);
        String fileName = "Test_EasyExcel";
        String sheetName = "测试";
        int[] mergeColIndex = {1,2,3};
        int mergeRowIndex = 1;
        ExcelUtil.writeExcel(response,cityInfoList,fileName,sheetName,mergeColIndex,mergeRowIndex,CityInfoCO.class);
    }

    @GetMapping("/getExcelTempalte")
    @ApiOperation(value = "获取excel模板",notes = "获取excel模板")
    public void getExcelTemplate(HttpServletResponse response){
        String fileName = "ExcelTempalte";
        String sheetName = "测试";
        ExcelUtil.writeExcel(response,null,fileName,sheetName,CityInfoCO.class);
    }
}
