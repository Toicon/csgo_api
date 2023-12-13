package com.csgo.modular.product.logic;

import com.csgo.constants.SystemConfigConstants;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.ExchangeRateMapper;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import com.csgo.modular.common.sms.logic.SmsLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawLogic {

    private final SystemConfigMapper systemConfigMapper;

    private final ExchangeRateMapper exchangeRateMapper;

    private final SmsLogic smsLogic;

    private final BigDecimal SPILL_RATE = new BigDecimal("0.05");

    public BigDecimal getBuyMaxPrice(BigDecimal maxWithdrawPrice) {
        return maxWithdrawPrice.multiply(BigDecimal.valueOf(6.5)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMaxWithdrawPrice(UserMessagePlus userMessage) {
//        ExchangeRate exchangeRate = exchangeRateMapper.queryAll().get(0);
        BigDecimal money = userMessage.getMoney();
        BigDecimal addPrice = money.multiply(SPILL_RATE);
        return money.add(addPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public void sendItemOfflineSms(GiftProductPlus giftProductPlus, UserPlus userPlus) {
        String content = giftProductPlus.getName().replaceAll("™", "");
        smsLogic.sendItemOfflineSms(userPlus.getPhone(), content);
    }

    public void sendItemSendSms(String phone, String giftProductName) {
        smsLogic.sendItemSendSms(phone, giftProductName);
    }

    public boolean isDisabled() {
        SystemConfig sysConfig = systemConfigMapper.get(SystemConfigConstants.WITHDRAW_DISABLED);
        return null != sysConfig && "TRUE".equals(sysConfig.getValue());
    }

}
