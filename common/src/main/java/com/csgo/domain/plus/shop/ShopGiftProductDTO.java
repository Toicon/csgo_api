package com.csgo.domain.plus.shop;

import com.csgo.domain.plus.gift.GiftProductPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class ShopGiftProductDTO extends GiftProductPlus {

    private int stock;
    private int typeId;
}
