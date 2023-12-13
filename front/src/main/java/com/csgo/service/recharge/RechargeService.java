package com.csgo.service.recharge;

import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.recharge.RechargeChannelType;
import com.csgo.mapper.plus.recharge.RechargeChannelMapper;
import com.csgo.mapper.plus.recharge.RechargeChannelPriceItemMapper;
import com.csgo.service.pay.AliService;
import com.csgo.service.pay.IPaymentService;
import com.csgo.support.StandardExceptionCode;
import com.echo.framework.platform.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Service
public class RechargeService {

    @Autowired
    private RechargeChannelMapper rechargeChannelMapper;
    @Autowired
    private RechargeChannelPriceItemMapper rechargeChannelPriceItemMapper;
    @Autowired
    private AliService aliService;

    public IPaymentService getChannelByType(RechargeChannelType type) {
        switch (type) {
            case ALI_PAY:
                return aliService;
            default:
                throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "渠道有误");
        }
    }

    public RechargeChannel get(int id) {
        return rechargeChannelMapper.selectById(id);
    }

    public RechargeChannelPriceItem getPriceItem(int priceItemId) {
        return rechargeChannelPriceItemMapper.selectById(priceItemId);
    }

    public List<RechargeChannel> findAll(boolean isApp) {
        return rechargeChannelMapper.find(isApp);
    }

    public List<RechargeChannelPriceItem> findPriceItemByChannelIds(List<Integer> channelIds) {
        return rechargeChannelPriceItemMapper.findByChannelIds(channelIds);
    }

    public RechargeChannelPriceItem findMinItem(int channelId) {
        return rechargeChannelPriceItemMapper.findMinItem(channelId);
    }

}
