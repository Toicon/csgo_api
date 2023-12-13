package com.csgo.condition.mine;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.mine.MineUserPrize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchMinePriceCondition extends Condition<MineUserPrize> {
    /**
     * 账号
     */
    private String userName;

    private BigDecimal prizePriceMin;

    private BigDecimal prizePriceMax;
}
