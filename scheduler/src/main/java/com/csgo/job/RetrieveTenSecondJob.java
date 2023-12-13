package com.csgo.job;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.plus.zbt.ZbtGiveOrder;
import com.csgo.domain.plus.zbt.ZbtGiveOrderStatus;
import com.csgo.service.ZbtGiveOrderService;
import com.csgo.support.ZBT.ZBTOrderInfo;
import com.csgo.support.ZBT.ZBTSteamInfo;
import com.csgo.support.ZBT.ZBTSteamResult;
import com.csgo.util.EmojiConverterUtil;
import com.csgo.util.HttpUtil2;
import com.echo.framework.scheduler.Job;
import com.echo.framework.support.jackson.json.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Admin on 2021/4/30
 */
@Service
@Slf4j
public class RetrieveTenSecondJob extends Job {
    @Autowired
    private ZBTProperties properties;
    @Autowired
    private ZbtGiveOrderService zbtGiveOrderService;

    @Override
    protected void execute() throws Throwable {
        log.info("--------每10秒查询报价情况------------------");
        List<ZbtGiveOrder> orders = zbtGiveOrderService.findByStatus(
                Arrays.asList(
                        ZbtGiveOrderStatus.PENDING,
                        ZbtGiveOrderStatus.PROCESSING,
                        ZbtGiveOrderStatus.WAITING
                ));
        if (CollectionUtils.isEmpty(orders)) {
            return;
        }
        orders.forEach(order -> {
            Map<String, Object> params = new HashMap<>();
            params.put("app-key", properties.getAppKey());
            params.put("language", "zh_CN");
            params.put("outTradeNo", order.getOutTradeNo());
            String orderDetail = HttpUtil2.doGet(properties.getOrderQuery(), params);
            ZBTOrderInfo orderInfo = JSON.fromJSON(orderDetail, ZBTOrderInfo.class);
            log.info("订单号：{}，报价查询情况：{}", order.getOutTradeNo(), orderDetail);
            if (StringUtils.hasText(order.getMsg()) || (null == orderInfo || null == orderInfo.getData())) { //订单不存在的情况
                order.setStatus(ZbtGiveOrderStatus.FAILURE);
                zbtGiveOrderService.update(order);
                return;
            }

            switch (orderInfo.getData().getStatus()) {
                case 0:
                case 1:
                    break;
                case 2:
                    order.setStatus(ZbtGiveOrderStatus.PROCESSING);
                    break;
                case 3:
                    order.setStatus(ZbtGiveOrderStatus.WAITING);
                    break;
                case 10:
                    order.setStatus(ZbtGiveOrderStatus.COMPLETE);
                    break;
                case 11:
                    order.setStatus(ZbtGiveOrderStatus.CANCEL);
                    order.setMsg(orderInfo.getData().getFailedDesc());
                    break;
                default:
                    break;
            }

            if (!StringUtils.hasText(order.getNickName())) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("app-key", properties.getAppKey());
                paramsMap.put("appId", properties.getAppId());
                paramsMap.put("type", 1);
                paramsMap.put("language", "zh_CN");
                paramsMap.put("tradeUrl", order.getSteamUrl());
                String steamDetail = HttpUtil2.doGet(properties.getSteamInfoUrl(), paramsMap);
                ZBTSteamResult steamResult = JSON.fromJSON(steamDetail, ZBTSteamResult.class);
                ZBTSteamInfo steamInfo = steamResult.getData();
                if (null != steamInfo && null != steamInfo.getSteamInfo()) {
                    String nickName = steamInfo.getSteamInfo().getNickName();
                    order.setNickName(EmojiConverterUtil.emojiConvert(nickName));
                    order.setAvatar(steamInfo.getSteamInfo().getAvatar());
                }
            }

            zbtGiveOrderService.update(order);
        });
    }
}
