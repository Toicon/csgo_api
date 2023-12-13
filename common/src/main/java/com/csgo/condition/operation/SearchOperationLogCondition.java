package com.csgo.condition.operation;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.log.OperationLog;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchOperationLogCondition extends Condition<OperationLog> {
    private String userName;
    private String keywords;
    private Date startDate;
    private Date endDate;
}
