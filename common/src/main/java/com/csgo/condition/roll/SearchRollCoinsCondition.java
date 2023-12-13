package com.csgo.condition.roll;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.roll.RollCoins;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchRollCoinsCondition extends Condition<RollCoins> {
    private Integer rollId;
}
