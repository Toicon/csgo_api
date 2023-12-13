package com.csgo.condition.blind;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.blind.BlindBoxRoomPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchBlindBoxRoomCondition extends Condition<BlindBoxRoomPlus> {

    private Integer userId;
}
