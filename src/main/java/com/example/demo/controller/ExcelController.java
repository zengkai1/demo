package com.example.demo.controller;


import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.demo.annotation.ExcelCondition;
import com.example.demo.co.excel.CityInfoCO;
import com.example.demo.config.excel.ExcelFillCellMergeStrategy;
import com.example.demo.dto.user.UserExcelTemplate;
import com.example.demo.util.ExcelCheckUtils;
import com.example.demo.util.ParseExcelUtil;
import com.example.demo.util.Result;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private MongoTemplate mongoTemplate;

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

    @PostMapping("/readExcel")
    @ApiOperation(value = "读取excel数据", notes = "读取excel数据")
    public Result readExcel(@ApiParam(value = "读取excel数据", required = true) MultipartFile file) throws IOException {
        //读取文件不能为空
        if (Objects.isNull(file)){
            return Result.handleFailure("读取文件不能为空");
        }
        //获取文件名称
        String filename = file.getOriginalFilename();
        //校验文件格式
        boolean isExcel = checkFilename(filename);
        if (!isExcel){
            return Result.handleFailure("读取失败，文件格式错误！");
        }
        //解析文件获取数据列表
        List<CityInfoCO> cityInfoCOS = ParseExcelUtil.toList(file, CityInfoCO.class);
        //mongoTemplate.insertAll(cityInfoCOS);
        return Result.ok().setData(cityInfoCOS);
    }

    @GetMapping("/writeExcel")
    @ApiOperation(value = "生成Excel数据", notes = "生成Excel数据")
    public void writeExcel( HttpServletResponse response){
        try {
            List<CityInfoCO> cityInfoList = mongoTemplate.findAll(CityInfoCO.class);
            //写入Excel
            ExcelWriter writer = new ExcelWriter(true,"生成Excel");
            //添加头部别名
            //writer.addHeaderAlias("",""); 单条写入
            //获取别名列表
            Map<String, String> headerAilas = ParseExcelUtil.getHeaderAilasHutool(CityInfoCO.class);
            writer.merge(headerAilas.size() - 1, "测试标题");
            cityInfoList.forEach(cityInfoCO -> {
            });
            //一次性写入
            //writer.write(cityInfoList,true);
            //设置列宽度
            for (int i = 0; i < headerAilas.size() ;i++){
                writer.setColumnWidth(i,20);
            }
            //设置行高度
            writer.setRowHeight(0,40);
            //设置内容字体
            Font font = writer.createFont();
            font.setBold(true);
            font.setColor(Font.COLOR_NORMAL);
            writer.getHeadCellStyle().setFont(font);
            //sheet命名
            writer.renameSheet("数据导出");
            //当前时间戳
            String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String fileName = "文件名称-"+currentTime+".xlsx";
            //下载excel
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
            // 关闭writer，释放内存
            writer.close();
            //此处记得关闭输出Servlet流
            IoUtil.close(out);
        } catch (Exception e){
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
     * 根据文件名校验文件格式是否为excel文件
     * @param filename ： 文件名
     * @return ： 格式是否正确，true 是 false 否
     */
    private boolean checkFilename(String filename) {
        //String fileSuffix = filename.substring(filename.lastIndexOf(".") + 1);
        String[] split = filename.split("\\.");
        String suffix = split[split.length-1];
        List<String> excelSuffixs = Arrays.asList("xls", "xlsx", "XLS", "XLSX");
        if (excelSuffixs.contains(suffix)){
            return true;
        }
        return false;
    }

}
