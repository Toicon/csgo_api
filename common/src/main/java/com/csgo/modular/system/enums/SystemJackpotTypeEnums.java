package com.csgo.modular.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum SystemJackpotTypeEnums {

    /**
     * 十连
     */
    TEN_DRAW(0, "十连"),

    /**
     * 十连测试
     */
    TEN_DRAW_ANCHOR(1, "十连测试"),


    /**
     * 模拟拆弹
     */
    NOV_BOMB(400, "模拟拆弹"),

    /**
     * 模拟拆弹测试
     */
    NOV_BOMB_ANCHOR(401, "模拟拆弹测试");

    public static final Integer ADD_SPARE_CODE = 10000;

    private final Integer code;

    private final String msg;

}
