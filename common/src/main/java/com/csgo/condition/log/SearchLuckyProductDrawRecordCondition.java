package com.csgo.condition.log;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.config.LuckyProductDrawRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchLuckyProductDrawRecordCondition extends Condition<LuckyProductDrawRecord> {
    private Integer userId;
    private String productName;
    private Integer profitSort;
    private Date startDate;
    private Date endDate;
}
