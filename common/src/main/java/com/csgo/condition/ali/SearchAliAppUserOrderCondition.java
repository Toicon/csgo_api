package com.csgo.condition.ali;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.ali.AliAppUserOrder;
import lombok.Data;

@Data
public class SearchAliAppUserOrderCondition extends Condition<AliAppUserOrder> {
    //订单状态（ 0：待发货 1：已发货）
    private Integer orderStatus;
    //用户id
    private Integer userId;
}
