package com.csgo.condition.code;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.code.ActivationCode;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchActivationCodeCondition extends Condition<ActivationCode> {
    private String cdKey;
    private String userName;
}
