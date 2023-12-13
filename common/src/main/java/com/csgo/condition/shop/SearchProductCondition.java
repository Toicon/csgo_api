package com.csgo.condition.shop;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.gift.GiftProductPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchProductCondition extends Condition<GiftProductPlus> {

    private String keywords;
}
