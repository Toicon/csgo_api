package com.csgo.condition.zbt;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.zbt.ZbtGiveOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchZbtGiveOrderCondition extends Condition<ZbtGiveOrder> {
    private String name;
    private String status;
    private String cb;
}
