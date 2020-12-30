package com.example.demo.constants.enums;

import lombok.Getter;

/**
 * <p>
 *  结果返回msg枚举
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 9:58
 */
@Getter
public enum ResultEnum {

    SUCCESS_CODE(0, "请求成功"),

    ERROR_CODE(1, "请求失败"),

    REQUEST_BODY_EMPTY(2, "请求json格式不正确"),

    REQUEST_LIMIT(3, "你的操作过于频繁,请休息一下吧!"),

    REPECT_SUBMIT(4, "系统正在处理、请勿重复提交!"),

    UPLOAD_SUCCESS_CODE(5, "上传成功"),

    UPLOAD_ERROR_CODE(6, "上传失败"),

    IMPORT_SUCCESS_CODE(7, "导入成功"),

    IMPORT_ERROR_CODE(8, "导入失败"),

    EXPORT_SUCCESS_CODE(7, "导出成功"),

    EXPORT_ERROR_CODE(8, "导出失败"),

    PLATE_NUMBER_EXIST_CODE(9, "车牌号已经存在"),

    TOKEN_LOSE_EFFICACY(10, "当前会话失效、请退出重登录再试"),

    EXCEL_UPLOAD_LIMIT(11, "当前系统正在处理刚刚上传Excel批量文件、请稍后再上传"),

    REDIS_ERROR(12, "操作Redis异常"),

    REQUEST_METHOD_ERROR(13, "请求方式不正确，请核对post、get、put、delete其中一种请求方式"),

    MYSQL_ERROR(14, "数据库操作出错"),

    USER_MANY_SCHOOL_ERROR(15, "该用户存在多个学校"),

    USER_PLATFORM_SCHOOL_ERROR(16, "该用户存在校级和平台级 请选择登录"),

    USER_MANY_ROLE_ERROR(10010, "该用户目前存在多种角色 请选择角色登录"),

    REQUEST_PARAM_NOT_EMPTY(100, "请求参数不能为空"),

    ACCESS_DENIED_ERROR(403, "令牌错误或权限不足、请联系管理员!"),

    QUERY_DATA_NOT_EXIST(404, "查询数据不存在"),

    REQUIRE_BODY_IS_MISSING(405, "请求Body中Json参数不能为空!"),

    TOMANY_DATA(411, "查询数据过多，建议减小时间粒度分批导出"),

    INTER_ERROR(500, "系统报错! 请联系管理员处理!"),

    NETWORK_ERROR(600, "网络错误，请重试"),

    PARAM_IS_ERROR(700, "输入的参数不正确"),


    TIMESTAMP_NOT_EMPTY(7003, "timestamp不能为空"),

    TIMESTAMP_TIME_OUT(7004, "timestamp 超时"),

    SIGN_ERROR(7005, "签名错误"),

    PARAM_SIGN_CHECK_ERROR(7006, "参数与签名核验错误"),

    DATA_ENCRYPT_ERROR(7007, "数据加密错误"),

    SIGN_NOT_EMPTY(7008, "签名不能为空"),

    REDIS_LOCK_FAIL(7009, "请求超时 请稍后再试!"),

    USER_NOT_ACTIVE(7010, "该账户已经被冻结 请联系管理员处理"),

    SCHOOL_NOT_ACTIVE(7011, "学校已冻结，请联系管理员!"),

    SCHOOL_UN_START_SERVICE_TIME(7012, "还未到服务开始日期!"),

    SCHOOL_UN_END_SERVICE_TIME(7013, "已过服务截至日期!"),

    ;


    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
