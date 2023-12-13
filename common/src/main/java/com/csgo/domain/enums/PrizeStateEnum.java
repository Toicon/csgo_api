package com.csgo.domain.enums;

/**
 * 挑战状态
 */
public enum PrizeStateEnum {

    YES(1, "成功"),
    NO(2, "失败"),
    OVER(3, "挑战结束");

    PrizeStateEnum(Integer code, String msg) {
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
