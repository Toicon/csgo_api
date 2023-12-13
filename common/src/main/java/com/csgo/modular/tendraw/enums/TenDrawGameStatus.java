package com.csgo.modular.tendraw.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum TenDrawGameStatus {

    /**
     * 进行中
     */
    ING(0, "进行中"),

    /**
     * 已完成
     */
    FINISHED(1, "已完成");

    private final Integer code;

    private final String msg;

}
