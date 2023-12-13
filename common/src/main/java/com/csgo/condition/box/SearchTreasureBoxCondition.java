package com.csgo.condition.box;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.box.TreasureBox;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchTreasureBoxCondition extends Condition<TreasureBox> {
    private String name;
    private String giftName;
}
