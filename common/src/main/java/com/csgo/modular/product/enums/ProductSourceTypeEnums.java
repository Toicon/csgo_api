package com.csgo.modular.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum ProductSourceTypeEnums {

    /**
     * 系统
     */
    SYS(0, "系统"),
    /**
     * IG
     */
    IG(1, "IG"),
    /**
     * 悠悠有品
     */
    YY(2, "悠悠有品"),

    ZBT(3, "ZBT");

    private final Integer code;

    private final String msg;

}
