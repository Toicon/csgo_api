package com.csgo.modular.bomb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum NovBombPlayType {

    /**
     * 进行中
     */
    FIRST(0, "第一次"),

    /**
     * 再次挑战
     */
    AGAIN(1, "再次挑战"),
    /**
     * 增加难度
     */
    DIFFICULTY(2, "增加难度");

    private final Integer code;

    private final String msg;

}
