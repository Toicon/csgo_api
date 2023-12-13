package com.csgo.condition.fish;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.fish.FishUserPrize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFishMyUserPrizeCondition extends Condition<FishUserPrize> {
    private Integer giftId;
    private Integer userId;
}
