package com.csgo.service.withdraw;

import com.csgo.config.properties.ZBTProperties;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.SystemConfigConstants;
import com.csgo.domain.ExchangeRate;
import com.csgo.domain.QuickBuyParamV2DTO;
import com.csgo.domain.enums.NotificationTemplateTypeEnum;
import com.csgo.domain.enums.WithdrawDeliveryMethod;
import com.csgo.domain.enums.WithdrawPropItemStatus;
import com.csgo.domain.enums.WithdrawPropStatus;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.withdraw.WithdrawProp;
import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.domain.plus.withdraw.WithdrawPropRelateDTO;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.ExchangeRateMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.mapper.plus.withdraw.WithdrawPropMapper;
import com.csgo.mapper.plus.withdraw.WithdrawPropRelateMapper;
import com.csgo.modular.product.enums.ProductKindEnums;
import com.csgo.modular.product.logic.WithdrawLogic;
import com.csgo.modular.product.util.GiftProductWithdrawUtil;
import com.csgo.service.MessageNotificationService;
import com.csgo.service.OrderRecordService;
import com.csgo.service.config.SystemConfigService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.support.ZBT.ItemPriceInfoRequest;
import com.csgo.support.ZBT.PriceInfo;
import com.csgo.support.ZBT.ZBTProductPrice;
import com.csgo.util.DateUtils;
import com.csgo.util.HttpsUtil2;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Admin on 2021/5/22
 */
@Service
@Slf4j
public class WithdrawPropZbtService {
    @Autowired
    private WithdrawPropMapper mapper;
    @Autowired
    private WithdrawPropRelateMapper relateMapper;
    @Autowired
    private UserMessagePlusMapper userMessageMapper;
    @Autowired
    private UserMessageGiftPlusMapper userMessageGiftMapper;
    @Autowired
    private ExchangeRateMapper rateMapper;
    @Autowired
    private UserPlusMapper userMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private MessageNotificationService messageNotificationService;
    @Autowired
    private ZBTProperties properties;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private WithdrawLogic withdrawLogic;


    @Transactional
    public List<String> withdraw(List<Integer> messageIds, Integer userId, String operator) {
        List<String> errMsgs = new ArrayList<>();
        if (withdrawLogic.isDisabled()) {
            errMsgs.add("取回功能维护中，00:00 恢复提取");
            return errMsgs;
        }
        List<OrderRecord> orderRecords = orderRecordService.findRecharge(userId, null, null, "2");
        BigDecimal rechargeAmount = orderRecords.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        SystemConfig systemConfig = systemConfigService.get(SystemConfigConstants.WITHDRAW_LIMIT);
        if (null == systemConfig) {
            errMsgs.add("系统错误，请联系客服");
            return errMsgs;
        }
        BigDecimal limitAmount = new BigDecimal(systemConfig.getValue());
        UserPlus userPlus = userMapper.selectById(userId);
        if (rechargeAmount.compareTo(limitAmount) < 0 && userPlus.getFlag() == GlobalConstants.RETAIL_USER_FLAG) {
            errMsgs.add("充值任意金额开通取回权限");
            return errMsgs;
        }
        List<UserMessagePlus> userMessagePluses = userMessageMapper.findByIdsAndUserId(messageIds, userId);
        if (CollectionUtils.isEmpty(userMessagePluses)) {
            errMsgs.add("提取饰品信息不存在，请刷新页面");
            return errMsgs;
        }

        userMessagePluses.forEach(item -> {
            if (ProductKindEnums.GIFT_KEY.getCode().equals(item.getProductKind())) {
                throw BizClientException.of(CommonBizCode.KEY_BOX_KEY_CANNOT_WITHDRAW);
            }
        });

        WithdrawProp withdrawProp = new WithdrawProp();
        withdrawProp.setStatus(WithdrawPropStatus.PENDING);
        withdrawProp.setUserId(userId);
        withdrawProp.setDrewDate(new Date());
        withdrawProp.setCb(operator);
        withdrawProp.setCt(new Date());
        mapper.insert(withdrawProp);


        // 增加限制，提取总金额超过充值的百分比（后台配置），则走后台审批，超过100%默认自动走后台审批
        BigDecimal withdrawAmount = userMessagePluses.stream().map(UserMessagePlus::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        SystemConfig config = systemConfigService.get(SystemConfigConstants.WITHDRAW_EXCEED);
        BigDecimal exceedAmount = withdrawAmount.multiply(new BigDecimal(config.getValue()).divide(BigDecimal.valueOf(100)));

        BigDecimal totalWithdrawAmount = userMessageMapper.findWithdrawAmountByUserId(userId);
        if (totalWithdrawAmount == null) {
            totalWithdrawAmount = BigDecimal.ZERO;
        }
        totalWithdrawAmount = totalWithdrawAmount.add(withdrawAmount);

        int withdrawCount = mapper.withdrawCount(userId, DateUtils.addDate(new Date(), -10, Calendar.MINUTE));
        if (rechargeAmount.compareTo(totalWithdrawAmount) < 0 //提取道具总金额超过充值总金额
                || exceedAmount.compareTo(rechargeAmount) > 0
                || withdrawCount > 3) { //10分钟内自动提取次数超过3次
            userMessagePluses.forEach(userMessage -> {
                if (!GlobalConstants.USER_MESSAGE_INVENTORY.equals(userMessage.getState())) {
                    errMsgs.add(userMessage.getProductName() + "不可取回");
                    return;
                }
                userMessage.setState("3");
                userMessageMapper.updateById(userMessage);
                withdrawRelate(WithdrawPropItemStatus.PENDING, withdrawProp, userMessage.getId(), null, null);
            });
            return errMsgs;
        }

        return withdraw(errMsgs, userPlus, withdrawProp, userMessagePluses);
    }

    private List<String> withdraw(List<String> errMsgs, UserPlus userPlus, WithdrawProp withdrawProp, List<UserMessagePlus> userMessagePluses) {
        AtomicReference<Boolean> canDraw = new AtomicReference<>(Boolean.FALSE);
        AtomicReference<Boolean> autoDraw = new AtomicReference<>(Boolean.FALSE);
        AtomicReference<Boolean> failDraw = new AtomicReference<>(Boolean.FALSE);
        Set<Integer> giftProductIds = userMessagePluses.stream().map(UserMessagePlus::getGiftProductId).collect(Collectors.toSet());
        Map<Integer, GiftProductPlus> giftProductPluses = giftProductPlusMapper.findByGiftProductIds(giftProductIds).stream().collect(Collectors.toMap(GiftProductPlus::getId, giftProductPlus -> giftProductPlus));

        userMessagePluses.forEach(userMessage -> {
            if (!GlobalConstants.USER_MESSAGE_INVENTORY.equals(userMessage.getState())) {
                errMsgs.add(userMessage.getProductName() + "不可取回");
                return;
            }
            GiftProductPlus giftProductPlus = giftProductPluses.get(userMessage.getGiftProductId());

            ItemPriceInfoRequest request = new ItemPriceInfoRequest();
            request.setAppId(properties.getAppId());
            request.setMarketHashNameList(new String[]{giftProductPlus.getEnglishName()});
            String result = HttpsUtil2.getJsonData(JSON.toJSON(request), properties.getPriceUrl());
            ZBTProductPrice productPrice = JSON.fromJSON(result, ZBTProductPrice.class);

            //zbt商品下架，直接走后台审核
            if (productPrice.getSuccess() == null || !productPrice.getSuccess() || CollectionUtils.isEmpty(productPrice.getData()) || null == productPrice.getData().get(0) || null == productPrice.getData().get(0).getPrice()
                    || !StringUtils.hasText(giftProductPlus.getZbtItemId())) {
                log.error("zbt product not exist:{}", giftProductPlus.getName());
                userMessage.setState("3");
                userMessageMapper.updateById(userMessage);
                withdrawRelate(WithdrawPropItemStatus.PENDING, withdrawProp, userMessage.getId(), null, null);
                canDraw.set(Boolean.TRUE);
                return;
            }
            List<PriceInfo> zbtPriceInfo = productPrice.getData();
            zbtPriceInfo.sort(Comparator.comparing(PriceInfo::getPrice));
            PriceInfo priceInfo = zbtPriceInfo.get(0);
            log.info("zbt price info:{}", JSON.toJSON(priceInfo));

            ExchangeRate exchangeRate = rateMapper.queryAll().get(0);
            BigDecimal spillAmount = withdrawLogic.getMaxWithdrawPrice(userMessage);

            //提取商品高于溢价则拒绝提货
            if (priceInfo.getPrice().compareTo(spillAmount) > 0) {
                messageNotificationService.add(userMessage.getUserId(), giftProductPlus.getName(), giftProductPlus.getName(), NotificationTemplateTypeEnum.SPILL_PRICE);
                log.error("zbt product spill price name:{}", giftProductPlus.getName());
                withdrawProp.setStatus(WithdrawPropStatus.FAILURE);
                withdrawRelate(WithdrawPropItemStatus.FAILURE, withdrawProp, userMessage.getId(), "zbt商品溢价", null);
                failDraw.set(Boolean.TRUE);
                errMsgs.add(userMessage.getProductName() + "商品溢价");
                return;
            }

            if (userMessage.getMoney().compareTo(exchangeRate.getExtractMoney()) <= 0 && userPlus.getFlag() == GlobalConstants.RETAIL_USER_FLAG) {
                this.autoDraw(userPlus, withdrawProp, autoDraw, userMessage, giftProductPlus, priceInfo, spillAmount);
                return;
            }

            UserMessageGiftPlus messageGiftList = userMessageGiftMapper.findByMessageId(userMessage.getId());
            if (null == messageGiftList) {
                errMsgs.add(userMessage.getProductName() + "商品已下架");
                return;
            }

            userMessage.setState("3");
            userMessageMapper.updateById(userMessage);

            withdrawRelate(WithdrawPropItemStatus.PENDING, withdrawProp, userMessage.getId(), null, null);
            canDraw.set(Boolean.TRUE);
        });
        if (!canDraw.get() && !autoDraw.get() && !failDraw.get()) {
            throw BizClientException.of(CommonBizCode.WITHDRAW_FAIL);
        }
        if (autoDraw.get()) {
            withdrawProp.setStatus(WithdrawPropStatus.AUTO);
        } else if (!canDraw.get() && failDraw.get()) {
            withdrawProp.setStatus(WithdrawPropStatus.FAILURE);
        }
        if (!WithdrawPropStatus.PENDING.equals(withdrawProp.getStatus())) {
            mapper.updateById(withdrawProp);
        }
        return errMsgs;
    }


    private void autoDraw(UserPlus userPlus, WithdrawProp withdrawProp, AtomicReference<Boolean> autoDraw, UserMessagePlus userMessage, GiftProductPlus giftProductPlus, PriceInfo priceInfo, BigDecimal spillAmount) {
        QuickBuyParamV2DTO dto = new QuickBuyParamV2DTO();
        dto.setTradeUrl(userPlus.getSteam());
        dto.setOutTradeNo(GiftProductWithdrawUtil.createNewOutTradeNo());
        if (null != priceInfo.getAutoDeliverPrice() && null != priceInfo.getManualDeliverPrice()) { //自动发货与人工发货都在溢价内
            if (BigDecimal.ZERO.compareTo(priceInfo.getAutoDeliverPrice()) == 0) {
                dto.setDelivery(1);
            } else if (BigDecimal.ZERO.compareTo(priceInfo.getManualDeliverPrice()) == 0) {
                dto.setDelivery(2);
            } else {
                dto.setDelivery(priceInfo.getAutoDeliverPrice().compareTo(spillAmount) > 0 ? 1 : 2);  //优先走自动发货
            }
        } else if (null == priceInfo.getAutoDeliverPrice() && null != priceInfo.getManualDeliverPrice()) {
            dto.setDelivery(1);
        } else if (null != priceInfo.getAutoDeliverPrice() && null == priceInfo.getManualDeliverPrice()) {
            dto.setDelivery(2);
        }
        dto.setItemId(giftProductPlus.getZbtItemId());
        dto.setMaxPrice(spillAmount);

        String result = HttpsUtil2.getJsonData(JSON.toJSON(dto), properties.getQuicklyBuy());
        Map<String, Object> extractProp = JSON.fromJSON(result, Map.class);
        String errMsg = null;
        if (!extractProp.get("errorCode").equals(0)) {
            errMsg = (String) extractProp.get("errorMsg");
        }

        withdrawRelate(WithdrawPropItemStatus.AUTO, withdrawProp, userMessage.getId(), errMsg, dto.getOutTradeNo());

        updateMessageGift(userMessage);

        autoDraw.set(Boolean.TRUE);
    }

    private void updateMessageGift(UserMessagePlus userMessage) {
        UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());
        userMessageGift.setUt(new Date());
        userMessageGift.setState(3);
        userMessageGift.setZbkDate(DateUtils.toStringDate(new Date()));
        userMessageGiftMapper.updateById(userMessageGift);
        userMessage.setState("3");
        userMessageMapper.updateById(userMessage);
    }

    private void withdrawRelate(WithdrawPropItemStatus status, WithdrawProp withdrawProp, Integer messageId, String message, String outTradeNo) {
        WithdrawPropRelate relate = new WithdrawPropRelate();
        relate.setDeliveryMethod(WithdrawDeliveryMethod.ZBT);
        relate.setStatus(status);
        relate.setPopId(withdrawProp.getId());
        relate.setMessageId(messageId);
        relate.setMessage(message);
        if (StringUtils.hasText(outTradeNo)) {
            relate.setOutTradeNno(outTradeNo);
        } else {
            relate.setOutTradeNno(GiftProductWithdrawUtil.createNewOutTradeNo());
        }
        relate.setCt(new Date());
        relate.setUt(new Date());
        relate.setCb(withdrawProp.getCb());
        relateMapper.insert(relate);
    }

    public List<WithdrawPropRelateDTO> findRelate(Integer userId) {
        return relateMapper.findByStatusAndUserId(userId);
    }


}
