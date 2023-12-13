package com.csgo.condition.accessory;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.accessory.LuckyProductDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchLuckyProductCondition extends Condition<LuckyProductDTO> {

    private String keywords;
    private String csgoType;
    private BigDecimal lowPrice;
    private BigDecimal highPrice;
    private int priceSort;
    private int typeId;
}
