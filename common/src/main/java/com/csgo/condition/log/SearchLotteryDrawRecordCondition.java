package com.csgo.condition.log;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.domain.plus.lucky.LotteryDrawType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchLotteryDrawRecordCondition extends Condition<LotteryDrawRecord> {
    private Integer userId;
    private LotteryDrawType type;
    private Date startDate;
    private Date endDate;
}
