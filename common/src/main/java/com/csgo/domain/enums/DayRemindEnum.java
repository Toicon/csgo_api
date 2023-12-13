package com.csgo.domain.enums;

/**
 * 今日不在提醒(0:提醒,1:不提醒)
 */
public enum DayRemindEnum {

    NO(1, "不提醒"),
    YES(0, "提醒");

    DayRemindEnum(Integer code, String msg) {
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
