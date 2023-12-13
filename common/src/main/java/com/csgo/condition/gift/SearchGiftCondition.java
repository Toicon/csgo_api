package com.csgo.condition.gift;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.gift.GiftPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/5/26
 */
@Setter
@Getter
public class SearchGiftCondition extends Condition<GiftPlus> {
    private String name;

    private Integer typeId;

    private boolean membershipGrade;
}
