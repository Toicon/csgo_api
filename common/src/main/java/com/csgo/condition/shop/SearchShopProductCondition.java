package com.csgo.condition.shop;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.shop.ShopGiftProductDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchShopProductCondition extends Condition<ShopGiftProductDTO> {

    private String giftProductName;
    private String sortType;
    private BigDecimal min;
    private BigDecimal max;
    private String exteriorName;
}
