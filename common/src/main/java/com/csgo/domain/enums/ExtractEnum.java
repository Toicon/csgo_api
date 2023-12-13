package com.csgo.domain.enums;

/**
 * 提取状态
 */
public enum ExtractEnum {

    YES(2, "已提取"),
    EXTRACTING(3, "提取中");

    ExtractEnum(Integer code, String msg) {
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
