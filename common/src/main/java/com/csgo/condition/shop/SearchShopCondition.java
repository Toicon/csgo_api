package com.csgo.condition.shop;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.shop.ShopDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchShopCondition extends Condition<ShopDTO> {

    private String keywords;

    private BigDecimal shopSpillPriceRate = new BigDecimal("1");

    private String csgoType;

    private BigDecimal lowPrice;

    private BigDecimal highPrice;

}
