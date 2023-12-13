package com.csgo.domain.enums;

/**
 * 是、否枚举
 */
public enum PassStateEnum {

    INIT(0, "闯关中"),
    YES(1, "通过"),
    NO(2, "不通过");

    PassStateEnum(Integer code, String msg) {
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
