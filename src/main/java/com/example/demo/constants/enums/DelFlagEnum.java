package com.example.demo.constants.enums;

import lombok.Getter;

/**
 * <p>
 *
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/16 11:32
 */
public enum  DelFlagEnum {

    YES(1, "是"),
    NO(0, "否");

    private Integer code;
    private String desc;

    DelFlagEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DelFlagEnum getEnumByCode(Integer code) {
        for (DelFlagEnum DelFlagEnum : DelFlagEnum.values()) {
            if (DelFlagEnum.getCode().equals(code)) {
                return DelFlagEnum;
            }
        }
        return null;
    }

    public static String getDesc(Integer code) {
        for (DelFlagEnum DelFlagEnum : DelFlagEnum.values()) {
            if (DelFlagEnum.getCode().equals(code)) {
                return DelFlagEnum.getDesc();
            }
        }
        return null;
    }
}
