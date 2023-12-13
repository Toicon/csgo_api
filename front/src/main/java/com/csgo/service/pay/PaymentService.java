package com.csgo.service.pay;

import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.order.OrderRecordStyle;
import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.modular.pay.hook.UserRechargeHookManage;
import com.csgo.service.OrderRecordService;
import com.csgo.service.membership.MembershipService;
import com.csgo.service.membership.MembershipTaskRecordService;
import com.csgo.service.user.UserService;
import com.csgo.support.CreateOrderRecordContext;
import com.csgo.support.GlobalConstants;
import com.csgo.support.OrderRecordBuilder;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.DateUtilsEx;
import com.echo.framework.platform.exception.ApiException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by Admin on 2021/6/21
 */
public abstract class PaymentService implements IPaymentService {
    static Integer goodIds;
    @Autowired
    private UserRechargeHookManage userRechargeHookManage;
    @Autowired
    OrderRecordService orderRecordService;
    @Autowired
    private OrderRecordBuilder orderRecordBuilder;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRecordMapper orderRecordMapper;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private MembershipTaskRecordService membershipTaskRecordService;

    @Override
    @Transactional
    public Map<String, String> pay(UserPlus user, RechargeChannel channel, RechargeChannelPriceItem priceItem, boolean isFirst, HttpServletRequest servletRequest) {
        boolean innerRecharge = userService.validateInnerRecharge(user.getId());
        OrderRecord orderRecord = createOrderRecord(user, OrderRecordStyle.valueOf(channel.getType().name()), priceItem, isFirst, innerRecharge);
        if (user.getFlag() == GlobalConstants.INTERNAL_USER_FLAG) {
            Map<String, String> innerMap = new HashMap<>();
            if (innerRecharge) {
                innerMap.put("countDown", "10000");
            } else {
                innerMap.put("OrderNo", orderRecord.getOrderNum());
            }
            String url = "https://xxxx.oss-cn-shenzhen.aliyuncs.com/qr-code.png";
            innerMap.put("img_url", url);
            return innerMap;
        }
        goodIds = priceItem.getGoodsId();
        Map<String, String> map = pay(orderRecord, channel, servletRequest);
        if (null != map.get("channelOrderNo")) {
            orderRecord.setChannelOrderNum(map.get("channelOrderNo"));
            orderRecordMapper.updateById(orderRecord);
        }
        map.put("OrderNo", orderRecord.getOrderNum());
        return map;
    }

    abstract Map<String, String> pay(OrderRecord orderRecord, RechargeChannel channel, HttpServletRequest servletRequest);

    @Override
    @Transactional
    public Map<String, String> mobilePay(UserPlus user, RechargeChannel channel, RechargeChannelPriceItem priceItem, boolean isFirst, HttpServletRequest servletRequest) {
        boolean innerRecharge = userService.validateInnerRecharge(user.getId());
        OrderRecord orderRecord = createOrderRecord(user, OrderRecordStyle.valueOf(channel.getType().name()), priceItem, isFirst, innerRecharge);
        if (user.getFlag() == GlobalConstants.INTERNAL_USER_FLAG) {
            Map<String, String> innerMap = new HashMap<>();
            if (innerRecharge) {
                innerMap.put("countDown", "10000");
            } else {
                innerMap.put("OrderNo", orderRecord.getOrderNum());
            }
            String url = "https://xxxx.oss-cn-shenzhen.aliyuncs.com/qr-code.png";
            innerMap.put("img_url", url);
            return innerMap;
        }
        goodIds = priceItem.getGoodsId();
        Map<String, String> map = mobilePay(orderRecord, channel, servletRequest);
        map.put("OrderNo", orderRecord.getOrderNum());
        return map;
    }

    abstract Map<String, String> mobilePay(OrderRecord orderRecord, RechargeChannel channel, HttpServletRequest servletRequest);

    private OrderRecord createOrderRecord(UserPlus user, OrderRecordStyle style, RechargeChannelPriceItem priceItem, boolean isFirst, boolean innerRecharge) {
//        if (!StringUtils.hasText(user.getSteam())) {
//            throw new BusinessException(ExceptionCode.NO_BINDING_ERROR);
//        }
        String mhtOrderNo = System.currentTimeMillis() + "" + RandomStringUtils.randomNumeric(6);
        return orderRecordBuilder.createOrderRecord(CreateOrderRecordContext.builder()
                .user(user)
                .price(priceItem.getPrice())
                .extraPrice(priceItem.getExtraPrice())
                .mhtOrderNo(mhtOrderNo)
                .style(style)
                .isFirst(isFirst)
                .innerRecharge(innerRecharge)
                .build());
    }

    void processSuccess(String mhtOrderN0, OrderRecord orderRecord) {
        orderRecord.setOrderStatus("2");
        orderRecord.setPaidTime(new Date());

        // 进行分佣
        UserPlus user = userService.get(orderRecord.getUserId());
        if (user != null) {
            OrderRecord firstRecharge = orderRecordService.findFirst(user.getId());
            if (null == firstRecharge && orderRecord.isFirst()) {
                orderRecord.setExtraPrice(orderRecord.getExtraPrice().add(orderRecord.getOrderAmount().multiply(new BigDecimal(0.1)).setScale(2, RoundingMode.HALF_UP)));
                orderRecord.setFirst(true);
            } else {
                orderRecord.setFirst(false);
            }
            user.setBalance(user.getBalance().add(orderRecord.getOrderAmount().add(orderRecord.getExtraPrice())));
            user.setPayMoney(user.getPayMoney().add(orderRecord.getPaidAmount()));
            userService.update(user);
            orderRecordBuilder.cost(user, orderRecord.getOrderAmount().add(orderRecord.getExtraPrice()));
            orderRecordBuilder.membershipHandler(membershipService.findByUserId(orderRecord.getUserId()), orderRecord.getOrderAmount(), orderRecord.isFirst());
            userRechargeHookManage.handleUserRechargeSuccess(user, orderRecord);
            if (user.getParentId() != null) {
                if (null != user.getInvitedDate() && user.getInvitedDate().before(new Date())) {
                    userService.userDistribution(mhtOrderN0, user.getId(), user.getParentId(), orderRecord.getOrderAmount(), 1);
                } else if (null == user.getInvitedDate()) { //兼容旧数据
                    userService.userDistribution(mhtOrderN0, user.getId(), user.getParentId(), orderRecord.getOrderAmount(), 1);
                }
            } else {
                if (new Date().before(DateUtilsEx.calDateByHour(user.getCreatedAt(), 24))) {
                    userService.userDistribution(mhtOrderN0, user.getId(), 0, orderRecord.getOrderAmount(), 1);
                }
            }

        }
    }

    String sendHttp(String url, Map<String, String> data) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
        httpPost.setConfig(requestConfig);


        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        // url格式编码
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(uefEntity);
            HttpResponse response = httpClient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            if (HttpStatus.SC_OK != status.getStatusCode()) {
                throw new Exception("Http请求异常,httpcode=" + status.getStatusCode());
            }

            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            throw new ApiException(StandardExceptionCode.USER_ERROR, "支付失败，请重新再试");
        }
    }

    String sendHttpObject(String url, Map<String, Object> data) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
        httpPost.setConfig(requestConfig);


        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        // url格式编码
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(uefEntity);
            HttpResponse response = httpClient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            if (HttpStatus.SC_OK != status.getStatusCode()) {
                throw new Exception("Http请求异常,httpcode=" + status.getStatusCode());
            }

            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            throw new ApiException(StandardExceptionCode.USER_ERROR, "支付失败，请重新再试");
        }
    }
}
