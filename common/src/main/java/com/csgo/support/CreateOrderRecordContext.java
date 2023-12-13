package com.csgo.support;

import java.math.BigDecimal;
import com.csgo.domain.plus.membership.Membership;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.order.OrderRecordStyle;
import com.csgo.domain.plus.user.UserPlus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Builder
@Getter
@Setter
public class CreateOrderRecordContext {

    private UserPlus user;
    private Membership membership;
    private BigDecimal price;
    private BigDecimal extraPrice;
    private String mhtOrderNo;
    private OrderRecordStyle style;
    private OrderRecord existRecord;
    private boolean forceRecharge;
    private boolean isFirst;
    private boolean innerRecharge;
}
