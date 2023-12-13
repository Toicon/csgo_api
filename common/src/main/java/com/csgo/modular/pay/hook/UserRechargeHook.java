package com.csgo.modular.pay.hook;

import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.user.UserPlus;

import java.math.BigDecimal;

/**
 * @author admin
 */
public interface UserRechargeHook {

    default void handleRechargeSuccess(UserPlus user, OrderRecord orderRecord) {
        Integer userId = user.getId();
        BigDecimal orderAmount = orderRecord.getOrderAmount();
        handleRechargeSuccess(userId, orderAmount);
    }

    void handleRechargeSuccess(Integer userId, BigDecimal orderAmount);

}
