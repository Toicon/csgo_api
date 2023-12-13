package com.csgo.domain.enums;

/**
 * 钓鱼-场次类型
 */
public enum FishSessionTypeEnum {

    PRIMARY(1, "初级"),
    MIDDLE(2, "中级"),
    SENIOR(3, "高级");

    FishSessionTypeEnum(Integer code, String msg) {
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
