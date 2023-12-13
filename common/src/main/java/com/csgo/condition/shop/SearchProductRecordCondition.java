package com.csgo.condition.shop;

import java.util.Date;
import com.csgo.condition.Condition;
import com.csgo.domain.plus.gift.GiftProductUpdateRecord;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchProductRecordCondition extends Condition<GiftProductUpdateRecord> {

    private String name;

    private Date startDate;

    private Date endDate;
}
