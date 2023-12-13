package com.csgo.condition.fish;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.fish.FishUserPrize;
import lombok.Data;

@Data
public class SearchFishPriceCondition extends Condition<FishUserPrize> {
    /**
     * 账号
     */
    private String userName;
}
