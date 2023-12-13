package com.csgo.condition.blind;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.blind.BlindBoxDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchBlindBoxCondition extends Condition<BlindBoxDTO> {

    private String keywords;
    private Integer typeId;

}
