package com.csgo.service.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.withdraw.SearchWithdrawPropCondition;
import com.csgo.config.ZBTProperties;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.domain.QuickBuyParamV2DTO;
import com.csgo.domain.enums.NotificationTemplateTypeEnum;
import com.csgo.domain.enums.WithdrawDeliveryMethod;
import com.csgo.domain.enums.WithdrawPropItemStatus;
import com.csgo.domain.enums.WithdrawPropStatus;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.withdraw.WithdrawProp;
import com.csgo.domain.plus.withdraw.WithdrawPropDTO;
import com.csgo.domain.plus.withdraw.WithdrawPropPriceDTO;
import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.service.LockService;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.mapper.plus.withdraw.WithdrawPropMapper;
import com.csgo.mapper.plus.withdraw.WithdrawPropRelateMapper;
import com.csgo.modular.ig.config.IgProperties;
import com.csgo.modular.product.logic.WithdrawLogic;
import com.csgo.modular.product.util.MoneyRateUtil;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.message.MessageNotificationService;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.support.GlobalConstants;
import com.csgo.support.ZBT.PriceInfo;
import com.csgo.support.ZBT.ZBTBean;
import com.csgo.support.ZBT.ZBTResult;
import com.csgo.util.DateUtils;
import com.csgo.util.HttpUtil2;
import com.csgo.util.HttpsUtil2;
import com.echo.framework.support.jackson.json.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by Admin on 2021/5/22
 */
@Service
@Slf4j
public class WithdrawPropService {
    @Autowired
    private WithdrawPropMapper withdrawPropMapper;
    @Autowired
    private WithdrawPropRelateMapper relateMapper;
    @Autowired
    private UserMessagePlusMapper userMessageMapper;
    @Autowired
    private UserMessageGiftPlusMapper userMessageGiftMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private MessageNotificationService messageNotificationService;

    @Autowired
    private IgProperties igProperties;
    @Autowired
    private WithdrawLogic withdrawLogic;

    @Lazy
    @Autowired
    private WithdrawPropService withdrawPropService;
    @Autowired
    private ZBTProperties zbtProperties;

    @Autowired
    private LockService lockService;

    public BigDecimal countSpendingTotal(Date startDate, Date endDate) {
        BigDecimal price = relateMapper.countSpendingTotal(startDate, endDate);
        return MoneyRateUtil.getUsdPrice(price);
    }

    public Page<WithdrawPropDTO> pagination(SearchWithdrawPropCondition condition) {
        return withdrawPropMapper.pagination(condition.getPage(), condition);
    }

    public List<WithdrawPropPriceDTO> findByUserIds(List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return withdrawPropMapper.findByUserIds(userIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateItem(Integer itemId, WithdrawPropItemStatus status, String name) {
        if (withdrawLogic.isDisabled()) {
            throw BizClientException.of(CommonBizCode.WITHDRAW_DISABLED);
        }
        WithdrawPropRelate item = relateMapper.selectById(itemId);

        String lockKey = LockConstant.LOCK_WITHDRAW_POP + item.getPopId();
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }

            WithdrawPropRelate relate = relateMapper.selectById(itemId);
            if (!relate.getStatus().equals(WithdrawPropItemStatus.PENDING)) {
                throw BizException.of("非审核状态，无法进行操作");
            }
            WithdrawProp withdrawProp = withdrawPropMapper.selectById(relate.getPopId());
            //假发货
            if (status.equals(WithdrawPropItemStatus.FAKE)) {
                processFake(name, relate, withdrawProp);
            } else if (status.equals(WithdrawPropItemStatus.REJECT)) {
                relate.setStatus(WithdrawPropItemStatus.REJECT);
                relate.setUb(name);
                reject(relate);

                updateStatus(withdrawProp, name, WithdrawPropStatus.REJECT);
            } else if (status.equals(WithdrawPropItemStatus.PASS)) {

                UserMessagePlus userMessage = userMessageMapper.selectById(relate.getMessageId());
                GiftProductPlus giftProductPlus = giftProductPlusMapper.selectById(userMessage.getGiftProductId());
                UserPlus userPlus = userPlusMapper.selectById(userMessage.getUserId());

                withdrawPropService.doWithdrawProp(withdrawProp, relate, userMessage, giftProductPlus, userPlus, name);
            } else {
                throw BizClientException.of(CommonBizCode.COMMON_TYPE_NOT_SUPPORT);
            }
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    public void updateBatchWithdraw(Integer propId, WithdrawPropStatus status, String operator) {
        if (withdrawLogic.isDisabled()) {
            throw BizClientException.of(CommonBizCode.WITHDRAW_DISABLED);
        }

        String lockKey = LockConstant.LOCK_WITHDRAW_POP + propId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }

            WithdrawProp withdrawProp = withdrawPropMapper.selectById(propId);
            List<WithdrawPropRelate> relates = relateMapper.findByPropIdAndStatus(propId);
            if (status.equals(WithdrawPropStatus.REJECT)) {
                relates.forEach(relate -> {
                    if (!relate.getStatus().equals(WithdrawPropItemStatus.PENDING)) {
                        log.info("当前道具发货状态已变更，订单号：{},状态：{}", relate.getOutTradeNno(), relate.getStatus());
                        return;
                    }
                    relate.setStatus(WithdrawPropItemStatus.REJECT);
                    reject(relate);
                });
                updateStatus(withdrawProp, operator, WithdrawPropStatus.REJECT);
            } else if (status.equals(WithdrawPropStatus.FAKE)) {
                relates.forEach(relate -> processFake(operator, relate, withdrawProp));
            } else if (status.equals(WithdrawPropStatus.PASS)) {
                List<Integer> messageIds = relates.stream().map(WithdrawPropRelate::getMessageId).collect(Collectors.toList());

                List<UserMessagePlus> messages = userMessageMapper.findByIds(messageIds);

                Map<Integer, UserMessagePlus> messageMap = messages.stream().collect(Collectors.toMap(UserMessagePlus::getId, userMessage -> userMessage));
                List<Integer> giftProductIds = messages.stream().map(UserMessagePlus::getGiftProductId).collect(Collectors.toList());

                Map<Integer, GiftProductPlus> giftProductPlusMap = giftProductPlusMapper.findByGiftProductIds(giftProductIds).stream().collect(Collectors.toMap(GiftProductPlus::getId, giftProductPlus -> giftProductPlus));

                List<Integer> userIds = messages.stream().map(UserMessagePlus::getUserId).collect(Collectors.toList());
                Map<Integer, UserPlus> userPlusMap = userPlusMapper.findByIds(userIds).stream().collect(Collectors.toMap(UserPlus::getId, userPlus -> userPlus));

                relates.forEach(relate -> {
                    try {
                        UserMessagePlus userMessage = messageMap.get(relate.getMessageId());
                        GiftProductPlus giftProductPlus = giftProductPlusMap.get(userMessage.getGiftProductId());
                        UserPlus userPlus = userPlusMap.get(userMessage.getUserId());

                        withdrawPropService.doWithdrawProp(withdrawProp, relate, userMessage, giftProductPlus, userPlus, operator);
                    } catch (Exception e) {
                        log.error("后台发货出错，订单号：{}，异常信息：{}", relate.getOutTradeNno(), e.getMessage());
                    }
                });
            }
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void doWithdrawProp(WithdrawProp withdrawProp, WithdrawPropRelate relate, UserMessagePlus userMessage, GiftProductPlus giftProductPlus, UserPlus userPlus, String operator) {
        if (!"3".equals(userMessage.getState())) {
            updateRelate(relate, WithdrawPropItemStatus.FAILURE, "当前背包道具已发生变化");
            messageNotificationService.addFailure(userPlus.getId(), giftProductPlus.getName(), giftProductPlus.getName(), "当前背包道具已发生变化");
            return;
        }

        // doWithDrawPropByYy(withdrawProp, relate, userMessage, giftProductPlus, userPlus, operator);
        // doWithDrawPropByIg(withdrawProp, relate, userMessage, giftProductPlus, userPlus, operator);
        doWithDrawPropByZbt(withdrawProp, relate, userMessage, giftProductPlus, userPlus, operator);
    }

    private void doWithDrawPropByIg(WithdrawProp withdrawProp, WithdrawPropRelate relate, UserMessagePlus userMessage, GiftProductPlus giftProductPlus, UserPlus userPlus, String operator) {
        BigDecimal maxWithdrawPrice = withdrawLogic.getMaxWithdrawPrice(userMessage);
        withdrawByIg(userPlus, userMessage, giftProductPlus, maxWithdrawPrice, relate);

        updateStatus(withdrawProp, operator, WithdrawPropStatus.PASS);
    }

    private void doWithDrawPropByZbt(WithdrawProp withdrawProp, WithdrawPropRelate relate, UserMessagePlus userMessage, GiftProductPlus giftProductPlus, UserPlus userPlus, String operator) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("app-key", zbtProperties.getAppKey());
        map.put("appId", zbtProperties.getAppId());
        map.put("keyword", giftProductPlus.getName());
        String resultJson = HttpUtil2.doGet(zbtProperties.getSearchUrl(), map);
        ZBTResult objectMap = new ZBTResult();
        objectMap = JSON.fromJSON(resultJson, objectMap.getClass());
        if (objectMap.getSuccess() == null || !objectMap.getSuccess() || CollectionUtils.isEmpty(objectMap.getData().getList()) || null == objectMap.getData().getList().get(0).getPriceInfo()) {
            messageNotificationService.add(userMessage.getUserId(), giftProductPlus.getName(), giftProductPlus.getName(), NotificationTemplateTypeEnum.GOODS_OFF_SHELVES);
            withdrawLogic.sendItemOfflineSms(giftProductPlus, userPlus);

            updateRelate(relate, WithdrawPropItemStatus.FAILURE, "zbt商品已下架");
            userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            userMessageMapper.updateById(userMessage);
        } else {
            List<ZBTBean> dataList = objectMap.getData().getList();
            dataList.sort(ZBTBean::compare);
            PriceInfo priceInfo = dataList.get(0).getPriceInfo();
            log.info("[zbt发货] price info:{}", JSON.toJSON(priceInfo));

            BigDecimal maxWithdrawPrice = withdrawLogic.getMaxWithdrawPrice(userMessage);
            this.withdrawByZbt(relate, userMessage, giftProductPlus, userPlus, priceInfo, maxWithdrawPrice);
        }
        updateStatus(withdrawProp, operator, WithdrawPropStatus.PASS);
    }

    /**
     * 假发货处理
     */
    private void processFake(String name, WithdrawPropRelate relate, WithdrawProp withdrawProp) {
        UserMessagePlus userMessage = userMessageMapper.selectById(relate.getMessageId());
        if ("2".equals(userMessage.getState())) {
            return;
        }
        UserMessageGift gift = new UserMessageGift();
        gift.setUserMessageId(userMessage.getId());
        UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());
        userMessageGift.setZbkMoney(userMessage.getMoney());
        userMessageGift.setUt(new Date());
        userMessageGift.setState(2);
        userMessageGift.setZbkDate(DateUtils.toStringDate(new Date()));
        userMessageGiftMapper.updateById(userMessageGift);

        userMessage.setState("2");
        userMessageMapper.updateById(userMessage);

        messageNotificationService.add(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), NotificationTemplateTypeEnum.DELIVERY_SUCCESS);

        int recordId = userMessageRecordService.add(userMessage.getUserId(), "饰品提取", "OUT");
        userMessageItemRecordService.add(recordId, userMessage.getId(), userMessage.getImg());

        relate.setStatus(WithdrawPropItemStatus.FAKE);
        relate.setUb(name);
        relateMapper.updateById(relate);

        updateStatus(withdrawProp, name, WithdrawPropStatus.PASS);
    }

    private void updateStatus(WithdrawProp withdrawProp, String operator, WithdrawPropStatus status) {
        List<WithdrawPropRelate> relates = relateMapper.findRelateByPropId(withdrawProp.getId());
        AtomicReference<Boolean> atomicReference = new AtomicReference<>(Boolean.TRUE);
        relates.forEach(relate -> {
            if (relate.getStatus().equals(WithdrawPropItemStatus.PENDING)) {
                atomicReference.set(Boolean.FALSE);
            }
        });
        if (atomicReference.get()) {
            withdrawProp.setStatus(status);
            withdrawProp.setUb(operator);
            withdrawProp.setUt(new Date());
            withdrawPropMapper.updateById(withdrawProp);
        }
    }

    /**
     * 拒绝提货
     */
    private void reject(WithdrawPropRelate relate) {
        UserMessagePlus userMessage = userMessageMapper.selectById(relate.getMessageId());
        if (!"3".equals(userMessage.getState())) {
            return;
        }
        userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        userMessageMapper.updateById(userMessage);
        GiftProductPlus giftProductPlus = giftProductPlusMapper.selectById(userMessage.getGiftProductId());
        messageNotificationService.addFailure(userMessage.getUserId(), giftProductPlus.getName(), giftProductPlus.getName(), "");
        relateMapper.updateById(relate);
    }

    private void processNotOrder(WithdrawPropRelate relate, UserMessagePlus userMessage) {
        UserMessageGiftPlus userMessageGift = userMessageGiftMapper.findByMessageId(userMessage.getId());
        userMessageGift.setState(0);
        userMessageGiftMapper.updateById(userMessageGift);
        userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        userMessageMapper.updateById(userMessage);

        String errMsg = relate.getMessage();
        if (StringUtils.hasText(errMsg) && errMsg.contains("余额不足")) {
            errMsg = "";
        }
        messageNotificationService.addFailure(userMessageGift.getUserId(), userMessageGift.getGiftProductName(), userMessageGift.getGiftProductName(), errMsg);
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

    private void withdrawByIg(UserPlus userPlus, UserMessagePlus userMessage, GiftProductPlus giftProductPlus, BigDecimal spillAmount, WithdrawPropRelate relate) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("partner_key", igProperties.getPartnerKey());
            params.put("public_key", igProperties.getPublicKey());
            String signParams = igProperties.getPartnerKey() + igProperties.getPublicKey() + igProperties.getSecretKey();
            params.put("sign", DigestUtils.md5Hex(signParams.getBytes(UTF_8)));
            params.put("app_id", igProperties.getAppId());

            params.put("market_hash_name", giftProductPlus.getEnglishName());

            log.info("IG拉取价格请求参数：{}", JSON.toJSON(params));
            String igQueryResult = HttpsUtil2.sendFormPost(igProperties.getQueryPriceUrl(), params);
            log.info("IG拉取价格响应结果：{}", igQueryResult);
            JSONObject jsonObject = new JSONObject(igQueryResult);
            int code = jsonObject.getInt("code");
            if (code != 1) {
                withdrawLogic.sendItemOfflineSms(giftProductPlus, userPlus);
                updateRelate(relate, WithdrawPropItemStatus.FAILURE, "道具已下架");
                processNotOrder(relate, userMessage);
                return;
            }
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray products = data.getJSONArray("products");
            if (null == products || products.length() == 0) {
                withdrawLogic.sendItemOfflineSms(giftProductPlus, userPlus);
                updateRelate(relate, WithdrawPropItemStatus.FAILURE, "道具已下架");
                processNotOrder(relate, userMessage);
                return;
            }
            JSONObject product = products.getJSONObject(0);
            if (product.getBigDecimal("min_price").compareTo(BigDecimal.ZERO) <= 0) {
                withdrawLogic.sendItemOfflineSms(giftProductPlus, userPlus);
                updateRelate(relate, WithdrawPropItemStatus.FAILURE, "道具已下架");
                processNotOrder(relate, userMessage);
                return;
            }

            params.put("track_link", URLEncoder.encode(userPlus.getSteam(), "UTF-8"));

            log.info("IG获取用户steamid请求：{}", params);
            String igSteamResult = HttpsUtil2.sendFormPost(igProperties.getSteamIdUrl(), params);
            JSONObject steamObj = new JSONObject(igSteamResult);
            int steamCode = steamObj.getInt("code");
            if (steamCode != 1) {
                updateRelate(relate, WithdrawPropItemStatus.FAILURE, "steam交易连接无效");
                processNotOrder(relate, userMessage);
                return;
            }

            JSONObject steamData = steamObj.getJSONObject("data");
            log.info("IG获取用户steamid结果：{}", steamData);
            String steamDate = steamData.getString("steam_date");
            if (!StringUtils.hasText(steamDate)) {
                updateRelate(relate, WithdrawPropItemStatus.FAILURE, "steam交易连接无效");
                processNotOrder(relate, userMessage);
                return;
            }

            params.put("buyer_track_link", URLEncoder.encode(userPlus.getSteam(), "UTF-8"));
            params.put("buyer_steam_uid", String.valueOf(steamData.getLong("steam_uid")));

            params.put("steam_date", String.valueOf(Objects.requireNonNull(DateUtils.stringToDate(steamDate)).getTime()).substring(0, 10));

            params.put("channal_order_num", relate.getOutTradeNno());
            params.put("max_price", withdrawLogic.getBuyMaxPrice(spillAmount).toString());
            int delivery = product.getBigDecimal("min_price").compareTo(product.getBigDecimal("ags_auto_min_price")) == 0 ? 1 : 2;
            params.put("delivery_type", String.valueOf(delivery));

            log.info("IG拉货请求参数：{}", JSON.toJSON(params));
            String igBuyResult = HttpsUtil2.sendFormPost(igProperties.getBuyUrl(), params);
            log.info("IG拉货响应结果：{},用户名：{}", igBuyResult, userPlus.getUserName());
            JSONObject buyObj = new JSONObject(igBuyResult);
            int buyCode = buyObj.getInt("code");
            String message = null;
            if (buyCode != 1) {
                log.info("ig buy error:{}", JSONObject.valueToString(buyObj));
                if (buyObj.has("message")) {
                    message = buyObj.getString("message");
                }
            }

            JSONObject buyData = null;
            if (buyObj.has("data")) {
                buyData = buyObj.getJSONObject("data");
            }

            updateMessageGift(userMessage);

            relate.setDeliveryMethod(WithdrawDeliveryMethod.IG);
            if (buyData != null && buyData.getLong("order_id") > 0 && buyData.getBigDecimal("order_total") != null) {
                relate.setOrderId(String.valueOf(buyData.getLong("order_id")));
                relate.setZbtPrice(buyData.getBigDecimal("order_total"));
            }
            updateRelate(relate, WithdrawPropItemStatus.PASS, message);
        } catch (UnsupportedEncodingException e) {
            log.error("ig withdraw exception:{}", e.getMessage());
            throw BizServerException.of(CommonBizCode.WITHDRAW_FAIL);
        }
    }

    private static String getMaxPrice(BigDecimal spillAmount) {
        BigDecimal price = spillAmount.multiply(BigDecimal.valueOf(6.5));

        BigDecimal lowPrice = BigDecimal.valueOf(2);
        if (BigDecimalUtil.lessThan(price, lowPrice)) {
            // 以最低2人民币的价格购买
            price = lowPrice;
        }
        return price.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private void withdrawByZbt(WithdrawPropRelate relate, UserMessagePlus userMessage, GiftProductPlus giftProductPlus, UserPlus user, PriceInfo priceInfo, BigDecimal spillAmount) {
        if (!relate.getDeliveryMethod().equals(WithdrawDeliveryMethod.ZBT)) {
            log.error("[zbt发货] relateId:{} deliveryMethod:{} 类型不正确", relate.getId(), relate.getDeliveryMethod());
            throw new BusinessException(ExceptionCode.WITHDRAW_ERROR);
        }
        if (!StringUtils.hasText(giftProductPlus.getZbtItemId())) {
            messageNotificationService.add(user.getId(), giftProductPlus.getName(), giftProductPlus.getName(), NotificationTemplateTypeEnum.GOODS_OFF_SHELVES);
            withdrawLogic.sendItemOfflineSms(giftProductPlus, user);

            updateRelate(relate, WithdrawPropItemStatus.FAILURE, "找不到对应zbt道具");
            userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            userMessageMapper.updateById(userMessage);
            return;
        }
        // 提取商品高于溢价拒绝提货
        if (priceInfo.getPrice().compareTo(spillAmount) > 0) {
            messageNotificationService.add(userMessage.getUserId(), giftProductPlus.getName(), giftProductPlus.getName(), NotificationTemplateTypeEnum.SPILL_PRICE);
            withdrawLogic.sendItemOfflineSms(giftProductPlus, user);

            updateRelate(relate, WithdrawPropItemStatus.FAILURE, "zbt商品溢价");
            userMessage.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            userMessageMapper.updateById(userMessage);
            return;
        }

        QuickBuyParamV2DTO dto = new QuickBuyParamV2DTO();
        dto.setTradeUrl(user.getSteam());
        dto.setOutTradeNo(relate.getOutTradeNno());
        if (null != priceInfo.getAutoDeliverPrice() && null != priceInfo.getManualDeliverPrice()) {
            if (BigDecimal.ZERO.compareTo(priceInfo.getAutoDeliverPrice()) == 0) {
                dto.setDelivery(1);
            } else if (BigDecimal.ZERO.compareTo(priceInfo.getManualDeliverPrice()) == 0) {
                dto.setDelivery(2);
            } else {
                //优先走自动发货
                dto.setDelivery(priceInfo.getAutoDeliverPrice().compareTo(spillAmount) > 0 ? 1 : 2);
            }
        } else if (null == priceInfo.getAutoDeliverPrice() && null != priceInfo.getManualDeliverPrice()) {
            //人工发货
            dto.setDelivery(1);
        } else if (null != priceInfo.getAutoDeliverPrice() && null == priceInfo.getManualDeliverPrice()) {
            //自动发货
            dto.setDelivery(2);
        }
        dto.setItemId(giftProductPlus.getZbtItemId());
        dto.setMaxPrice(spillAmount);

        String result = HttpsUtil2.getJsonData(JSON.toJSON(dto), zbtProperties.getQuicklyBuy());

        Map<String, Object> extractProp = JSON.fromJSON(result, Map.class);
        String errMsg = null;
        if (!extractProp.get("errorCode").equals(0)) {
            errMsg = (String) extractProp.get("errorMsg");
        }
        updateRelate(relate, WithdrawPropItemStatus.PASS, errMsg);
        updateMessageGift(userMessage);
    }

    private void updateRelate(WithdrawPropRelate relate, WithdrawPropItemStatus status, String message) {
        relate.setStatus(status);
        relate.setMessage(message);
        relate.setUt(new Date());
        relateMapper.updateById(relate);
    }

}
