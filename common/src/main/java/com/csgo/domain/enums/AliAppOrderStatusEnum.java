package com.csgo.domain.enums;

/**
 * APP小程序-订单状态
 */
public enum AliAppOrderStatusEnum {

    INIT(0, "待发货"),
    SEND(1, "已发货");

    AliAppOrderStatusEnum(Integer code, String msg) {
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
