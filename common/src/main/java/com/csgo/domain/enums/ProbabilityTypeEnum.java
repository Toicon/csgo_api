package com.csgo.domain.enums;

/**
 * 概率类型(0:普通权重,价格权重)
 */
public enum ProbabilityTypeEnum {

    COMMON(0, "普通权重"),
    PRICE(1, "价格权重");

    ProbabilityTypeEnum(Integer code, String msg) {
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
