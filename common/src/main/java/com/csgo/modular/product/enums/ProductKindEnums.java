package com.csgo.modular.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum ProductKindEnums {

    /**
     * 普通饰品
     */
    NORMAL(0, "普通饰品"),
    /**
     * 钥匙
     */
    GIFT_KEY(1, "礼包钥匙");

    private final Integer code;

    private final String msg;

}
