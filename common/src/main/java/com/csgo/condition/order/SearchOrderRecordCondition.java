package com.csgo.condition.order;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.order.OrderRecord;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/5/4
 */
@Setter
@Getter
public class SearchOrderRecordCondition extends Condition<OrderRecord> {
    private Integer userId;

    private String state;

    private String orderNum;

    private String name;

    private String flag;

    private Date startDate;

    private Date endDate;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;
}
