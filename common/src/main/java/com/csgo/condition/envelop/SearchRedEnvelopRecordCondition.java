package com.csgo.condition.envelop;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchRedEnvelopRecordCondition extends Condition<RedEnvelopRecord> {

    private Integer userId;

    private Integer envelopItemId;

    private List<Integer> envelopItemIds;
}
