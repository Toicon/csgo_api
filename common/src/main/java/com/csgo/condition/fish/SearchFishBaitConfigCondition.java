package com.csgo.condition.fish;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.fish.FishBaitConfig;
import lombok.Data;

@Data
public class SearchFishBaitConfigCondition extends Condition<FishBaitConfig> {
    /**
     * 鱼饵名称
     */
    private String baitName;

    /**
     * 场次类型(1:初级，2:中级，3:高级)
     */
    private Integer sessionType;
}
