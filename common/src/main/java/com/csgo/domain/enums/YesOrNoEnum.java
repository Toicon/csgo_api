package com.csgo.domain.enums;

/**
 * 是、否枚举
 */
public enum YesOrNoEnum {

    YES(1, "是"),
    NO(0, "否");

    YesOrNoEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
