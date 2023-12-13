package com.csgo.service.pay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.csgo.config.properties.AliProperties;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelType;
import com.csgo.service.lock.RedissonLockService;
import com.csgo.support.StandardExceptionCode;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 2021/6/21
 */
@Slf4j
@Service
public class AliService extends PaymentService {
    @Autowired
    private AliProperties aliProperties;

    @Autowired
    private RedissonLockService redissonLockService;

    @Override
    Map<String, String> pay(OrderRecord orderRecord, RechargeChannel channel, HttpServletRequest servletRequest) {
        String mhtOrderNo = orderRecord.getOrderNum();
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliProperties.getAppId(), aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliProperties.getNotifyUrl());
        request.setReturnUrl(aliProperties.getReturnUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderRecord.getOrderNum());
        bizContent.put("total_amount", orderRecord.getPaidAmount());
        bizContent.put("subject", "csgoskins饰品");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                Map<String, String> resMap = new HashMap<>();
                resMap.put("formDate", response.getBody());
                resMap.put("OrderNo", mhtOrderNo);
                return resMap;
            }
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值失败，请联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值失败，请联系管理员");
        }
    }

    @Override
    Map<String, String> mobilePay(OrderRecord orderRecord, RechargeChannel channel, HttpServletRequest servletRequest) {
        String mhtOrderNo = orderRecord.getOrderNum();
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliProperties.getAppId(), aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setNotifyUrl(aliProperties.getNotifyUrl());
        request.setReturnUrl(aliProperties.getReturnUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderRecord.getOrderNum());
        bizContent.put("total_amount", orderRecord.getPaidAmount());
        bizContent.put("subject", "csgoskins饰品");
        bizContent.put("product_code", "QUICK_WAP_WAY");

        request.setBizContent(bizContent.toString());
        try {
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                Map<String, String> resMap = new HashMap<>();
                resMap.put("formDate", response.getBody());
                resMap.put("OrderNo", mhtOrderNo);
                return resMap;
            }
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值失败，请联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值失败，请联系管理员");
        }
    }

    @Override
    @Transactional
    public void callback(HttpServletRequest request, HttpServletResponse resp, RechargeChannelType type) {
        try {
            Map<String, String> params = getRequestParamMap(request);
            log.info("回调请求:{}", JSON.toJSON(params));
            String orderNo = params.get("out_trade_no");
            OrderRecord orderRecord = orderRecordService.queryOrderNum(orderNo);
            if (null == orderNo) {
                log.error("订单不存在，单号:{}", orderNo);
                return;
            }
            if (!type.name().equals(orderRecord.getStyle().name())) {
                log.error("订单提交与回调不一致，回调：{}，提交{}，单号:{}", type.name(), orderRecord.getStyle().name(), orderNo);
                return;
            }
            String lockKey = "callback:ali:" + orderNo;
            RLock rLock = null;
            try {
                rLock = redissonLockService.acquire(lockKey, 5, TimeUnit.SECONDS);
                if (rLock == null) {
                    log.warn("重复回调：{}，单号:{}", type.name(), orderNo);
                    return;
                }
                if ("TRADE_SUCCESS".equals(params.get("trade_status")) || "TRADE_FINISHED".equals(params.get("trade_status"))) {
                    if (new BigDecimal(params.get("receipt_amount")).compareTo(orderRecord.getPaidAmount()) == 0) {
                        processSuccess(orderNo, orderRecord);
                        orderRecord.setChannelOrderNum(params.get("trade_no"));
                        orderRecordService.update(orderRecord);
                    }
                } else if ("TRADE_CLOSED".equals(params.get("trade_status"))) {
                    if (!"2".equals(orderRecord.getOrderStatus())) {
                        orderRecord.setOrderStatus("3");
                        orderRecordService.update(orderRecord);
                    }
                }
            } finally {
                redissonLockService.releaseLock(lockKey, rLock);
            }
        } catch (Exception e) {
            log.error("异步回调错误：{}", e.getMessage());
        }
    }

    private Map getRequestParamMap(HttpServletRequest request) {
        Map map = new HashMap();
        //得到枚举类型的参数名称，参数名称若有重复的只能得到第一个
        Enumeration enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String paramName = (String) enums.nextElement();
            String paramValue = request.getParameter(paramName);
            //形成键值对应的map
            map.put(paramName, paramValue);
        }
        return map;
    }
}
