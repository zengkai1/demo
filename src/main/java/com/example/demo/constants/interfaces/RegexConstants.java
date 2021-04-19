package com.example.demo.constants.interfaces;

/**
 * <p>
 *  常用系统正则常量
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/18 16:46
 */
public interface RegexConstants {

    /**
     * 账号规则
     */
    String ACCOUNT_NAME = "^[\\u4E00-\\u9FA5A-Za-z0-9]{8,16}$";

    /**
     * 账号规则msg
     */
    String ACCOUNT_NAME_MSG = "账号格式不正确，只允许输入8-16位中英文数字";

    /**
     * 备注
     */
    String BACKUP = "^[\\u4E00-\\u9FA5A-Za-z0-9，。,.'“@？?!！$%()（）-]{0,120}$";

    /**
     * 姓名 未限制长度
     */
    String NAME = "^[a-zA-Z· \\u4e00-\\u9fa5]+$";

    /**
     * 打印机名称
     */
    String PRINTER_NAME = "^[\\u4E00-\\u9FA5A-Za-z0-9!@#$%^&*()_+=|`~{};':\",./?<>]{1,20}";

    /**
     * 许可证编码
     */
    String LICENCE_CODE = "^[\\u4E00-\\u9FA5A-Za-z0-9·，。,.]+$";

    /**
     * 许可证地址
     */
    String LICENCE_ADDRESS = "^[\\u4E00-\\u9FA5A-Za-z0-9·，。,.]+$";

    /**
     * 金额校验
     */
    String CHECK_AMOUNT = "^(([1-9][0-9]*)|(([0]\\.\\d{1,2}|[1-9][0-9]*\\.\\d{1,2})))$";

    /**
     * 法人 20个字以内的中英文
     */
    String LICENCE_LEGALER = "^[\\u4E00-\\u9FA5A-Za-z·\\s]{1,20}";

    /**
     * 联系人
     */
    String CONTACT_NAME = "^[A-Za-z0-9\\u4e00-\\u9fa5·]+$";

    /**
     * 门店名称
     */
    String STORE_NAME = "^[A-Za-z0-9\\u4e00-\\u9fa5!@#$%^&*_+|,./]+$";

    /**
     * 折扣百分比（限制输入0-10内的数，小数点后面最多两位小数）
     */
    String DISCOUNT_PERCENTAGE = "^((\\d|9)(\\.\\d{1,2})|[1-9]|10)$";

    /**
     * 折扣百分比 MSG（限制输入0-10内的数，小数点后面最多两位小数）
     */
    String DISCOUNT_PERCENTAGE_MSG = "折扣百分比限制输入0-10内的数，小数点后面最多两位小数";

    /**
     * 常用名称校验 只能输入中文英文数字和下划线
     */
    String COMMON_NAME = "^[a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+$";

    /**
     * 优惠券数目 -1或1-9999整数
     */
    String COUPONNUM= "^(-1|[1-9][0-9]{0,3})$";

    /**
     * 手机号校验
     */
    String MOBILE_CHECK = "^1[3-9]\\d{9}$";

    /**
     * 手机号校验
     */
    String MOBILE_CHECK_MSG = "您输入的手机号格式不正确！";

    /**
     * 优惠券金额 1-999,允许两位小数
     */
    String COUPON_MONEY = "^[1-9]\\d{0,2}(\\.\\d{1,2})?$";

    /**
     * 优惠券金额 1-999,允许两位小数
     */
    String COUPON_MONEY_MSG = "优惠券金额输入范围1-999，允许两位小数";

    /**
     * 邮箱校验
     */
    String EMAIL = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

    /**
     * 邮箱校验msg
     */
    String EMAIL_MSG = "邮箱格式不正确";
}

