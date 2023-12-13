package com.csgo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.csgo.modular.product.util.GiftProductWithdrawUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.QuickBuyParamV2DTO;
import com.csgo.domain.plus.zbt.ZbtGiveOrder;
import com.csgo.domain.plus.zbt.ZbtGiveOrderStatus;
import com.csgo.domain.plus.zbt.ZbtGiveOrderType;
import com.csgo.mapper.plus.zbt.ZbtGiveOrderMapper;
import com.csgo.util.HttpsUtil2;
import com.echo.framework.support.jackson.json.JSON;

/**
 * Created by Admin on 2021/7/19
 */
@Service
public class ZbtGiveOrderService {
    @Autowired
    private ZbtGiveOrderMapper mapper;
    @Autowired
    private ZBTProperties properties;

    @Transactional
    public void insert(String name, String itemId, String steamUrl, BigDecimal price, ZbtGiveOrderType type, String cb) {

        ZbtGiveOrder zbtGiveOrder = new ZbtGiveOrder();
        zbtGiveOrder.setOutTradeNo(GiftProductWithdrawUtil.createNewOutTradeNo());
        zbtGiveOrder.setSteamUrl(steamUrl);
        zbtGiveOrder.setName(name);
        zbtGiveOrder.setItemId(itemId);
        zbtGiveOrder.setPrice(price);
        zbtGiveOrder.setType(type);
        zbtGiveOrder.setCb(cb);

        //调用ZBT购买接口
        QuickBuyParamV2DTO dto = new QuickBuyParamV2DTO();
        dto.setTradeUrl(steamUrl);
        dto.setOutTradeNo(zbtGiveOrder.getOutTradeNo());
        dto.setDelivery(type.equals(ZbtGiveOrderType.AUTO) ? 2 : 1);
        dto.setItemId(itemId);
        dto.setMaxPrice(price);

        String result = HttpsUtil2.getJsonData(JSON.toJSON(dto), properties.getQuicklyBuy());
        Map<String, Object> extractProp = JSON.fromJSON(result, Map.class);
        if (extractProp.get("errorCode").equals(0)) {
            result = result.substring(result.indexOf("\"data\":") + 7, result.lastIndexOf(",\"errorCode\":"));
            Map<String, Object> stringObjectMap = JSON.fromJSON(result, Map.class);
            String orderId = stringObjectMap.get("orderId").toString();
            zbtGiveOrder.setOrderId(orderId);
        } else {
            zbtGiveOrder.setMsg((String) extractProp.get("errorMsg"));
        }

        zbtGiveOrder.setStatus(ZbtGiveOrderStatus.PENDING);
        zbtGiveOrder.setCt(new Date());
        mapper.insert(zbtGiveOrder);
    }

    public List<ZbtGiveOrder> findByStatus(List<ZbtGiveOrderStatus> statuses) {
        return mapper.findByStatus(statuses);
    }

    @Transactional
    public void update(ZbtGiveOrder order) {
        mapper.updateById(order);
    }
}
