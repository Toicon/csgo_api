package com.csgo.service.pay;

import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.recharge.RechargeChannelType;
import com.csgo.domain.plus.user.UserPlus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author admin
 */
public interface IPaymentService {

    Map<String, String> pay(UserPlus user, RechargeChannel channel, RechargeChannelPriceItem priceItem, boolean isFirst, HttpServletRequest servletRequest);

    Map<String, String> mobilePay(UserPlus user, RechargeChannel channel, RechargeChannelPriceItem priceItem, boolean isFirst, HttpServletRequest servletRequest);

    void callback(HttpServletRequest request, HttpServletResponse resp, RechargeChannelType rechargeChannelType);
}
