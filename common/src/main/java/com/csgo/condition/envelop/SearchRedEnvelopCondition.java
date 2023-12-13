package com.csgo.condition.envelop;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.envelop.RedEnvelop;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchRedEnvelopCondition extends Condition<RedEnvelop> {
    private String name;
}
