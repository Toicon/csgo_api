package com.csgo.condition.roll;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.roll.RollGiftPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchRollGiftPlusCondition extends Condition<RollGiftPlus> {
    private Integer rollId;
}
