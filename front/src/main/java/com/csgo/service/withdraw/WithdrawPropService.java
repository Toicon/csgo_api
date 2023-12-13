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
import com.csgo.modular.ig.config.IgProperties;
import com.csgo.modular.ig.service.IgService;
import com.csgo.modular.product.enums.ProductKindEnums;
import com.csgo.modular.product.logic.WithdrawLogic;
import com.csgo.modular.product.util.GiftProductWithdrawUtil;
import com.csgo.service.MessageNotificationService;
import com.csgo.service.OrderRecordService;
import com.csgo.service.config.SystemConfigService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.support.ZBT.PriceInfo;
import com.csgo.util.DateUtils;
import com.csgo.util.HttpsUtil2;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
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
    private IgProperties igProperties;
    @Autowired
    private WithdrawLogic withdrawLogic;
    @Autowired
    private IgService igService;

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

//        // 全部走后台审核
//        userMessagePluses.forEach(userMessage -> {
//            if (!GlobalConstants.USER_MESSAGE_INVENTORY.equals(userMessage.getState())) {
//                errMsgs.add(userMessage.getProductName() + "不可取回");
//                return;
//            }
//            userMessage.setState("3");
//            userMessageMapper.updateById(userMessage);
//            withdrawRelate(WithdrawPropItemStatus.PENDING, withdrawProp, userMessage.getId(), null, null);
//        });
//        return errMsgs;

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
            log.info("[提取] 走后台审核 userId:{}", userId);
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
            BigDecimal igMinPrice = igService.getIgProduct(giftProductPlus);
            // 商品下架，直接走后台审核
            if (igMinPrice == null) {
                log.error("[IG溢价查询不存在] id:{} name:{} ", giftProductPlus.getId(), giftProductPlus.getName());
                userMessage.setState("3");
                userMessageMapper.updateById(userMessage);
                withdrawRelate(WithdrawPropItemStatus.PENDING, withdrawProp, userMessage.getId(), null, null);
                canDraw.set(Boolean.TRUE);
                return;
            }

            ExchangeRate exchangeRate = rateMapper.queryAll().get(0);
            BigDecimal spillAmount = withdrawLogic.getMaxWithdrawPrice(userMessage);
            BigDecimal buyMaxPrice = withdrawLogic.getBuyMaxPrice(spillAmount);
            log.info("[IG价格] id:{} name:{} igMinPrice:{} buyMaxPrice:{}", giftProductPlus.getId(), giftProductPlus.getName(), igMinPrice, buyMaxPrice);
            //提取商品高于溢价则拒绝提货
            if (igMinPrice.compareTo(buyMaxPrice) > 0) {
                messageNotificationService.add(userMessage.getUserId(), giftProductPlus.getName(), giftProductPlus.getName(), NotificationTemplateTypeEnum.SPILL_PRICE);
                log.error("[IG溢价] id:{} name:{} igMinPrice:{} buyMaxPrice:{}", giftProductPlus.getId(), giftProductPlus.getName(), igMinPrice, buyMaxPrice);
                withdrawProp.setStatus(WithdrawPropStatus.FAILURE);
                withdrawRelate(WithdrawPropItemStatus.FAILURE, withdrawProp, userMessage.getId(), "ig商品溢价", null);
                failDraw.set(Boolean.TRUE);
                errMsgs.add(userMessage.getProductName() + "商品溢价");
                return;
            }

            if (userMessage.getMoney().compareTo(exchangeRate.getExtractMoney()) <= 0 && userPlus.getFlag() == GlobalConstants.RETAIL_USER_FLAG) {
                log.info("[IG自动发货] id:{} name:{} money:{} extractMoney:{}", giftProductPlus.getId(), giftProductPlus.getName(), userMessage.getMoney(), exchangeRate.getExtractMoney());
                String errM = drawByIg(userPlus, withdrawProp, userMessage, giftProductPlus, spillAmount);
                if (StringUtils.hasText(errM)) {
                    withdrawProp.setStatus(WithdrawPropStatus.FAILURE);
                    withdrawRelate(WithdrawPropItemStatus.FAILURE, withdrawProp, userMessage.getId(), errM, null);
                    failDraw.set(Boolean.TRUE);
                    errMsgs.add("发货失败,请联系客服!");
                } else {
                    autoDraw.set(Boolean.TRUE);
                }
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

    private String drawByIg(UserPlus userPlus, WithdrawProp withdrawProp, UserMessagePlus userMessage, GiftProductPlus giftProductPlus, BigDecimal spillAmount) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("partner_key", igProperties.getPartnerKey());
            params.put("public_key", igProperties.getPublicKey());
            String signParams = igProperties.getPartnerKey() + igProperties.getPublicKey() + igProperties.getSecretKey();
            params.put("sign", DigestUtils.md5Hex(signParams.getBytes(UTF_8)));
            params.put("app_id", igProperties.getAppId());

            params.put("market_hash_name", giftProductPlus.getEnglishName());

            log.info(" IG拉取价格请求参数{}", JSON.toJSON(params));

            String igQueryResult = HttpsUtil2.sendFormPost(igProperties.getQueryPriceUrl(), params);
            log.info("IG拉取价格响应结果：{}", igQueryResult);
            JSONObject jsonObject = new JSONObject(igQueryResult);
            int code = jsonObject.getInt("code");
            if (code != 1) {
                return jsonObject.getString("message");
            }
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray products = data.getJSONArray("products");
            if (null == products || products.length() == 0) {
                return "道具已下架";
            }
            JSONObject product = products.getJSONObject(0);
            if (product.getBigDecimal("min_price").compareTo(BigDecimal.ZERO) <= 0) {
                return "最低价格错误";
            }

            params.put("track_link", URLEncoder.encode(userPlus.getSteam(), "UTF-8"));
            params.remove("market_hash_name");
            log.info("IG获取用户steamid请求：{}", params);

            String igSteamResult = HttpsUtil2.sendFormPost(igProperties.getSteamIdUrl(), params);
            JSONObject steamObj = new JSONObject(igSteamResult);
            int steamCode = steamObj.getInt("code");
            if (steamCode != 1) {
                return steamObj.getString("message");
            }

            params.remove("track_link");

            JSONObject steamData = steamObj.getJSONObject("data");
            log.info("IG获取用户steamid结果：{}", steamData);
            String steamDate = steamData.getString("steam_date");
            if (!StringUtils.hasText(steamDate)) {
                return "steam交易连接无效";
            }
            params.put("market_hash_name", giftProductPlus.getEnglishName());
            params.put("buyer_track_link", URLEncoder.encode(userPlus.getSteam(), "UTF-8"));
            params.put("buyer_steam_uid", String.valueOf(steamData.getLong("steam_uid")));

            params.put("steam_date", String.valueOf(Objects.requireNonNull(DateUtils.stringToDate(steamDate)).getTime()).substring(0, 10));

            String tradeNo = GiftProductWithdrawUtil.createNewOutTradeNo();
            params.put("channal_order_num", tradeNo);
            params.put("max_price", withdrawLogic.getBuyMaxPrice(spillAmount).toString());
            int delivery = product.getBigDecimal("min_price").compareTo(product.getBigDecimal("ags_auto_min_price")) == 0 ? 1 : 2;
            params.put("delivery_type", String.valueOf(delivery));

            log.info(" ig buy params:{}", JSON.toJSON(params));

            log.info("IG拉货请求参数：{}", JSON.toJSON(params));
            String igBuyResult = HttpsUtil2.sendFormPost(igProperties.getBuyUrl(), params);
            log.info("IG拉货响应结果：{},用户名：{}", igBuyResult, userPlus.getUserName());
            JSONObject buyObj = new JSONObject(igBuyResult);
            int buyCode = buyObj.getInt("code");

            if (buyCode != 1) {
                log.info("ig buy error:{}", JSONObject.valueToString(buyObj));
                return buyObj.getString("message");
            }

            JSONObject buyData = buyObj.getJSONObject("data");

            updateMessageGift(userMessage);

            WithdrawPropRelate relate = new WithdrawPropRelate();
            relate.setStatus(WithdrawPropItemStatus.AUTO);
            relate.setDeliveryMethod(WithdrawDeliveryMethod.IG);
            relate.setPopId(withdrawProp.getId());
            relate.setMessageId(userMessage.getId());
            relate.setOutTradeNno(tradeNo);
            relate.setOrderId(String.valueOf(buyData.getLong("order_id")));
            relate.setZbtPrice(buyData.getBigDecimal("order_total"));
            relate.setCt(new Date());
            relate.setUt(new Date());
            relate.setCb(withdrawProp.getCb());
            relateMapper.insert(relate);
        } catch (UnsupportedEncodingException e) {
            log.error("ig withdraw exception:{}", e.getMessage());
            return "接口错误！";
        }
        return null;
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
        relate.setDeliveryMethod(WithdrawDeliveryMethod.IG);
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
