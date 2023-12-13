package com.csgo.condition.roll;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.roll.RollUserPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchRollUserCondition extends Condition<RollUserPlus> {
    private Integer rollId;
    private Integer flag;
    private String userName;

}
