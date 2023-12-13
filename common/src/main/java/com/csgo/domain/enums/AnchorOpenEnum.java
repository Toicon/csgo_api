package com.csgo.domain.enums;

/**
 * 测试奖池开箱开关
 */
public enum AnchorOpenEnum {

    YES("1", "是"),
    NO("0", "否");

    AnchorOpenEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;

    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
