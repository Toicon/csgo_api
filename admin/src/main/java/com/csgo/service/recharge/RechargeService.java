package com.csgo.service.recharge;

import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.order.OrderRecordStyle;
import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.user.RechargeRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.mapper.plus.recharge.RechargeChannelMapper;
import com.csgo.mapper.plus.recharge.RechargeChannelPriceItemMapper;
import com.csgo.mapper.plus.user.RechargeRecordMapper;
import com.csgo.support.CreateOrderRecordContext;
import com.csgo.support.GlobalConstants;
import com.csgo.support.OrderRecordBuilder;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.request.user.InsertRechargeRecordRequest;
import com.echo.framework.platform.exception.ApiException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
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
    private OrderRecordMapper orderRecordMapper;
    @Autowired
    private OrderRecordBuilder orderRecordBuilder;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Transactional
    public void recharge(UserPlus user, InsertRechargeRecordRequest request, String cb) {
        if (GlobalConstants.INTERNAL_USER_FLAG == user.getFlag()) {
            String mhtOrderNo = System.currentTimeMillis() + "" + RandomStringUtils.randomNumeric(6);
            orderRecordBuilder.createOrderRecord(CreateOrderRecordContext.builder()
                    .user(user)
                    .price(request.getPrice())
                    .extraPrice(BigDecimal.ZERO)
                    .mhtOrderNo(mhtOrderNo)
                    .style(OrderRecordStyle.valueOf(request.getStyle()))
                    .build());
            return;
        }

        RechargeRecord rechargeRecord = new RechargeRecord();
        BeanUtils.copyProperties(request, rechargeRecord);
        rechargeRecord.setBalance(user.getBalance());
        rechargeRecord.setCt(new Date());
        rechargeRecord.setCb(cb);
        String mhtOrderNo = System.currentTimeMillis() + "" + RandomStringUtils.randomNumeric(6);
        CreateOrderRecordContext.CreateOrderRecordContextBuilder context = CreateOrderRecordContext.builder()
                .user(user)
                .mhtOrderNo(mhtOrderNo)
                .forceRecharge(true)
                .style(OrderRecordStyle.valueOf(request.getStyle()));
        if (!StringUtils.isEmpty(request.getOrderNum())) {
            mhtOrderNo = request.getOrderNum();
            OrderRecord orderRecord = orderRecordMapper.get(request.getOrderNum());
            if (orderRecord == null || GlobalConstants.ORDER_PAY_SUCCESS.equals(orderRecord.getOrderStatus())) {
                throw new ApiException(StandardExceptionCode.USER_RECHARGE_FAILURE, "订单号有误");
            }
            orderRecord.setUpdateTime(new Date());
            orderRecord.setOrderStatus(GlobalConstants.ORDER_PAY_SUCCESS);
            orderRecordMapper.updateById(orderRecord);
            context.existRecord(orderRecord);
            rechargeRecord.setPrice(orderRecord.getOrderAmount());
        } else {
            context.price(request.getPrice()).extraPrice(BigDecimal.ZERO);
        }
        orderRecordBuilder.createOrderRecord(context.build());
        rechargeRecord.setOrderNum(mhtOrderNo);
        rechargeRecordMapper.insert(rechargeRecord);
    }

    @Transactional
    public void insert(RechargeChannel rechargeChannel, String name) {
        rechargeChannel.setCb(name);
        rechargeChannel.setCt(new Date());
        rechargeChannelMapper.insert(rechargeChannel);
    }

    @Transactional
    public void insertPriceItem(RechargeChannelPriceItem item, String name) {
        item.setCb(name);
        item.setCt(new Date());
        rechargeChannelPriceItemMapper.insert(item);
    }

    public RechargeChannel get(int id) {
        return rechargeChannelMapper.selectById(id);
    }

    public RechargeChannelPriceItem getPriceItem(int priceItemId) {
        return rechargeChannelPriceItemMapper.selectById(priceItemId);
    }

    public List<RechargeChannel> findAll() {
        return rechargeChannelMapper.selectList(null);
    }

    public List<RechargeChannelPriceItem> findPriceItemByChannelIds(List<Integer> channelIds) {
        return rechargeChannelPriceItemMapper.findByChannelIds(channelIds);
    }

    @Transactional
    public void update(RechargeChannel rechargeChannel, String name) {
        rechargeChannel.setUb(name);
        rechargeChannel.setUt(new Date());
        rechargeChannelMapper.updateById(rechargeChannel);
    }

    @Transactional
    public void updatePriceItem(RechargeChannelPriceItem item, String name) {
        item.setUb(name);
        item.setUt(new Date());
        rechargeChannelPriceItemMapper.updateById(item);
    }

    @Transactional
    public void delete(int channelId) {
        rechargeChannelMapper.deleteById(channelId);
        List<RechargeChannelPriceItem> items = rechargeChannelPriceItemMapper.findByChannelIds(Collections.singletonList(channelId));
        items.forEach(item -> rechargeChannelPriceItemMapper.deleteById(item.getId()));
    }

    @Transactional
    public void deletePriceItem(int priceItemId) {
        rechargeChannelPriceItemMapper.deleteById(priceItemId);
    }
}
