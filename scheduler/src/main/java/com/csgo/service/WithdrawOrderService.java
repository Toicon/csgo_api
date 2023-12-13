package com.csgo.service;

import cn.hutool.core.util.StrUtil;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.enums.NotificationTemplateTypeEnum;
import com.csgo.domain.enums.WithdrawPropItemStatus;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.withdraw.WithdrawPropRelateMapper;
import com.csgo.modular.ig.config.IgProperties;
import com.csgo.modular.product.logic.WithdrawLogic;
import com.csgo.service.message.MessageNotificationService;
import com.csgo.service.message.UserMessageItemRecordService;
import com.csgo.service.message.UserMessageRecordService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.ZBT.OfferInfoDTO;
import com.csgo.support.ZBT.ZBTOrderInfo;
import com.csgo.util.DateUtils;
import com.csgo.util.HttpUtil2;
import com.csgo.util.HttpsUtil2;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author admin
 */
@Slf4j
@Service
public class WithdrawOrderService {

    @Autowired
    private WithdrawPropRelateMapper withdrawPropRelateMapper;
    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private UserMessageGiftPlusMapper userMessageGiftMapper;
    @Autowired
    private MessageNotificationService messageNotificationService;
    @Autowired
    private WithdrawLogic withdrawLogic;
    @Autowired
    private IgProperties igProperties;
    @Autowired
    private ZBTProperties properties;

    @Transactional(rollbackFor = Exception.class)
    public void syncStatusByIg(WithdrawPropRelate entity) {
        Map<String, String> params = new HashMap<>();
        params.put("partner_key", igProperties.getPartnerKey());
        params.put("public_key", igProperties.getPublicKey());
        String signParams = igProperties.getPartnerKey() + igProperties.getPublicKey() + igProperties.getSecretKey();
        params.put("sign", DigestUtils.md5Hex(signParams.getBytes(UTF_8)));
        params.put("app_id", igProperties.getAppId());
        params.put("partner_order_number", entity.getOutTradeNno());

        String igQueryResult = HttpsUtil2.sendFormPost(igProperties.getOrderUrl(), params);
        log.info("ig订单号：{}，报价查询情况：{}", entity.getOutTradeNno(), igQueryResult);
        JSONObject jsonObject = new JSONObject(igQueryResult);
        int code = jsonObject.getInt("code");
        if (code == -1) {
            log.error("ig订单号：{}，报价查询发生异常：{}", entity.getOutTradeNno(), igQueryResult);
            return;
        }
        if (code != 1) {
            processNotOrder(entity);
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray orders = data.getJSONArray("orders");
        JSONObject object = orders.getJSONObject(0);

        UserMessagePlus userMessage = userMessagePlusMapper.selectById(entity.getMessageId());
        UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());

        switch (object.getInt("status")) {
            case 1: //订单已取消
                String errMsg = "订单已取消";
                processCancel(entity, errMsg);
                return;
            case 2: //等待卖家发货
                if (!WithdrawPropItemStatus.WAITING.equals(entity.getStatus())) {
                    entity.setStatus(WithdrawPropItemStatus.WAITING);
                    withdrawPropRelateMapper.updateById(entity);
                }
                break;
            case 4: //待买家接收报价
                if (entity.getStatus().equals(WithdrawPropItemStatus.RETRIEVE)) {
                    break;
                }
                params.put("order_id", entity.getOrderId());
                String tradeOfferResult = HttpsUtil2.sendFormPost(igProperties.getTradeOfferUrl(), params);
                log.info("IG获取报价ID结果:{}", tradeOfferResult);
                JSONObject tradeOfferObject = new JSONObject(tradeOfferResult);
                int tradeOfferCode = tradeOfferObject.getInt("code");
                if (tradeOfferCode != 1) {
                    return;
                }
                JSONObject tradeOfferData = tradeOfferObject.getJSONObject("data");
                if (tradeOfferData != null && !tradeOfferData.isNull("trade_offer")) {
                    String tradeOfferId = tradeOfferData.getString("trade_offer");
                    entity.setSteamUrl(properties.getSteamUrl() + tradeOfferId);
                    entity.setStatus(WithdrawPropItemStatus.RETRIEVE);
                    withdrawPropRelateMapper.updateById(entity);
                    //发送发货通知
                    messageNotificationService.add(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), NotificationTemplateTypeEnum.DELIVERY_SUCCESS);

                    withdrawLogic.sendItemSendSms(userMessageGift.getPhone(), userMessageGift.getGiftProductName());
                }
                break;
            case 5: //已完成
                processFinish(entity);
            default:
                break;
        }
    }

    private void processFinish(WithdrawPropRelate entity) {
        log.info("[YY订单][已完成] result:{}", JSON.toJSON(entity));

        UserMessagePlus userMessage = userMessagePlusMapper.selectById(entity.getMessageId());
        UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());

        userMessage.setState("2");
        userMessagePlusMapper.updateById(userMessage);

        //背包流水记录
        int recordId = userMessageRecordService.add(userMessage.getUserId(), "饰品提取", "OUT");
        userMessageItemRecordService.add(recordId, userMessage.getId(), userMessage.getImg());
        messageNotificationService.add(userMessageGift.getUserId(), userMessageGift.getGiftProductName(),
                userMessageGift.getGiftProductName(), NotificationTemplateTypeEnum.RETRIEVE_SUCCESS);

        userMessageGift.setZbkMoney(entity.getZbtPrice());
        userMessageGift.setState(2);
        userMessageGift.setZbkDate(DateUtils.dateToString(new Date()));
        userMessageGift.setUt(new Date());
        userMessageGiftMapper.updateById(userMessageGift);

        entity.setStatus(WithdrawPropItemStatus.RECEIPTED);
        withdrawPropRelateMapper.updateById(entity);
    }

    private void processCancel(WithdrawPropRelate entity, String statusMsg) {
        log.info("[YY订单][已取消] result:{}", JSON.toJSON(entity));

        UserMessagePlus userMessage = userMessagePlusMapper.selectById(entity.getMessageId());
        UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());

        userMessageGift.setState(0);
        userMessageGiftMapper.updateById(userMessageGift);

        userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        userMessagePlusMapper.updateById(userMessage);

        messageNotificationService.addFailure(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), statusMsg);

        entity.setStatus(WithdrawPropItemStatus.FAILURE);
        entity.setMessage(statusMsg);
        withdrawPropRelateMapper.updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncStatusByZbt(WithdrawPropRelate relate) {
        Map<String, Object> params = new HashMap<>();
        params.put("app-key", properties.getAppKey());
        params.put("language", "zh_CN");
        params.put("outTradeNo", relate.getOutTradeNno());
        String orderDetail = HttpUtil2.doGet(properties.getOrderQuery(), params);
        ZBTOrderInfo orderInfo = JSON.fromJSON(orderDetail, ZBTOrderInfo.class);
        log.info("zbt订单号：{}，报价查询情况：{}", relate.getOutTradeNno(), orderDetail);
        if (StringUtils.hasText(relate.getMessage()) && (null == orderInfo || null == orderInfo.getData())) { //订单不存在的情况
            processNotOrder(relate);
            return;
        }
        //未查到订单的或者订单还未处理的先跳过
        if (!orderInfo.getSuccess() || orderInfo.getData().getStatus() < 10) {
            if (null != orderInfo.getData()) {
                if (relate.getStatus().equals(WithdrawPropItemStatus.RETRIEVE)) {
                    return;
                }
                OfferInfoDTO offerInfo = orderInfo.getData().getOfferInfoDTO();
                if ((!StringUtils.hasText(relate.getSteamUrl()) || relate.getSteamUrl().substring(relate.getSteamUrl().length() - 1).equals("0")) && null != offerInfo.getTradeOfferId() && !offerInfo.getTradeOfferId().equals("0")) {
                    relate.setSteamUrl(properties.getSteamUrl() + offerInfo.getTradeOfferId());
                    relate.setStatus(WithdrawPropItemStatus.RETRIEVE);
                    relate.setOrderId(orderInfo.getData().getOrderId());
                    relate.setZbtPrice(this.getZbtRatePrice(orderInfo.getData().getPrice()));
                    withdrawPropRelateMapper.updateById(relate);

                    UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(relate.getMessageId());
                    //发送发货通知
                    messageNotificationService.add(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), NotificationTemplateTypeEnum.DELIVERY_SUCCESS);
                    withdrawLogic.sendItemSendSms(userMessageGift.getPhone(), userMessageGift.getGiftProductName());
                }
            }
            return;
        }
        UserMessagePlus userMessage = userMessagePlusMapper.selectById(relate.getMessageId());
        UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());
        if (10 == orderInfo.getData().getStatus()) {
            userMessage.setState("2");
            userMessagePlusMapper.updateById(userMessage);
            //背包流水记录
            int recordId = userMessageRecordService.add(userMessage.getUserId(), "饰品提取", "OUT");
            userMessageItemRecordService.add(recordId, userMessage.getId(), userMessage.getImg());
            messageNotificationService.add(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), NotificationTemplateTypeEnum.RETRIEVE_SUCCESS);

            userMessageGift.setZbkMoney(orderInfo.getData().getPrice());
            userMessageGift.setState(2);
            userMessageGift.setZbkDate(DateUtils.dateToString(new Date()));
            userMessageGift.setUt(new Date());
            userMessageGiftMapper.updateById(userMessageGift);

            relate.setOrderId(orderInfo.getData().getOrderId());
            relate.setZbtPrice(this.getZbtRatePrice(orderInfo.getData().getPrice()));
            relate.setStatus(WithdrawPropItemStatus.RECEIPTED);
            withdrawPropRelateMapper.updateById(relate);
        } else if (11 == orderInfo.getData().getStatus()) {
            userMessageGift.setState(0);
            userMessageGiftMapper.updateById(userMessageGift);
            userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            userMessagePlusMapper.updateById(userMessage);
            String errMsg = orderInfo.getData().getFailedDesc();
            messageNotificationService.addFailure(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), errMsg);

            relate.setStatus(WithdrawPropItemStatus.FAILURE);
            relate.setOrderId(orderInfo.getData().getOrderId());
            relate.setZbtPrice(this.getZbtRatePrice(orderInfo.getData().getPrice()));
            relate.setMessage(errMsg);
            withdrawPropRelateMapper.updateById(relate);
        }
    }

    /**
     * 处理未成单的情况
     */
    public void processNotOrder(WithdrawPropRelate relate) {
        UserMessagePlus userMessage = userMessagePlusMapper.selectById(relate.getMessageId());
        if ("3".equals(userMessage.getState())) {
            UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());
            userMessageGift.setState(0);
            userMessageGiftMapper.updateById(userMessageGift);
            userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            userMessagePlusMapper.updateById(userMessage);
            String errMsg = relate.getMessage();
            if (StringUtils.hasText(errMsg) && errMsg.contains("余额不足")) {
                errMsg = "";
            }
            messageNotificationService.addFailure(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), errMsg);
        }

        relate.setStatus(WithdrawPropItemStatus.FAILURE);
        withdrawPropRelateMapper.updateById(relate);
    }

    public BigDecimal getZbtRatePrice(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(6.5)).setScale(2, RoundingMode.HALF_UP);
    }
}
