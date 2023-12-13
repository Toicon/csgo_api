package com.csgo.domain.enums;

/**
 * 汇付宝支付签约状态
 */
public enum TradeStatusEnum {
    INIT(0, "签约中"),
    YES(1, "已签约"),
    NO(2, "签约失败"),
    CANCEL(3, "签约解绑");

    TradeStatusEnum(Integer code, String msg) {
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
