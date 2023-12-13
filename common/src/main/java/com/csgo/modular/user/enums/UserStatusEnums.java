package com.csgo.modular.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnums {

    /**
     * 正常
     */
    NORMAL(0, "正常"),
    /**
     * 冻结 (待兼容)
     */
    FROZEN(1, "冻结"),
    /**
     * 注销
     */
    CANCEL(2, "注销");

    private final Integer code;

    private final String msg;

}
