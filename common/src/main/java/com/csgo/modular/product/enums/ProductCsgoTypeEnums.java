package com.csgo.modular.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum ProductCsgoTypeEnums {

    /**
     * 普通饰品
     */
    GIFT_KEY("GIFT_KEY", "礼包钥匙");

    private final String type;

    private final String msg;

}
