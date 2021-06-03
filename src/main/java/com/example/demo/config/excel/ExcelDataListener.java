package com.example.demo.config.excel;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import com.baomidou.mybatisplus.extension.api.Assert;
import com.google.common.collect.Lists;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *  Excel数据监听器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年05月27日 14:25
 */
public class ExcelDataListener<T> extends AnalysisEventListener<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataListener.class);

    /**
     * 正文起始行
     */
    private Integer headRowNumber;

    /**
     * 合并单元格
     */
    private List<CellExtra> extraMergeInfoList = Lists.newArrayList();

    /**
     * 解析的数据
     */
    List<T> list = new ArrayList<T>();

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private MongoTemplate mongoTemplate;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param mongoTemplate
     */
    public ExcelDataListener(MongoTemplate mongoTemplate,Integer headRowNumber) {
        this.mongoTemplate = mongoTemplate;
        this.headRowNumber = headRowNumber;
    }

    /**
     * 加上存储数据库
     */
    public List<T> getData() {
        return list;
    }

    /**
     * 获取合并单元格列表
     * @return 单元格列表
     */
    public List<CellExtra> getExtraMergeInfoList() {
        return extraMergeInfoList;
    }

    /**
     * 这个每一条数据解析都会来调用
     * @param data
     * @param analysisContext
     */
    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        LOGGER.info("解析到一条数据:{}", JSONUtil.toJsonStr(data));
        list.add(data);
    }

    /**
     * 所有数据解析完成了 都会来调用
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        //saveData();
        LOGGER.info("{}条数据,所有数据解析完成！", list.size());
    }


    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
        mongoTemplate.insertAll(list);
        LOGGER.info("存储数据库成功！");
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        switch (extra.getType()) {
            case COMMENT: {
                LOGGER.info("额外信息是批注,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(), extra.getColumnIndex(),
                        extra.getText());
                break;
            }
            case HYPERLINK: {
                if ("Sheet1!A1".equals(extra.getText())) {
                    LOGGER.info("额外信息是超链接,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(),
                            extra.getColumnIndex(), extra.getText());
                } else if ("Sheet2!A1".equals(extra.getText())) {
                    LOGGER.info(
                            "额外信息是超链接,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{},"
                                    + "内容是:{}",
                            extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                            extra.getLastColumnIndex(), extra.getText());
                } else {
                    Assert.fail("Unknown hyperlink!");
                }
                break;
            }
            case MERGE: {
/*                LOGGER.info(
                        "额外信息是合并单元格,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{}",
                        extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                        extra.getLastColumnIndex());*/
                if (extra.getRowIndex() >= headRowNumber) {
                    extraMergeInfoList.add(extra);
                }
                break;
            }
            default: {
            }
        }
    }
}
