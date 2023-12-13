package com.csgo.condition.blind;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.blind.BlindBoxProductDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchBlindBoxProductCondition extends Condition<BlindBoxProductDTO> {

    private String keywords;
    private int blindBoxId;

}
