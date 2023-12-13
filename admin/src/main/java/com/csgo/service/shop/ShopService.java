package com.csgo.service.shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopCondition;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.shop.Shop;
import com.csgo.domain.plus.shop.ShopDTO;
import com.csgo.domain.plus.shop.ShopStatus;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.shop.ShopMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.support.ZBT.PriceInfo;
import com.csgo.support.ZBT.ZBTResult;
import com.csgo.util.HttpUtil2;
import com.echo.framework.support.jackson.json.JSON;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
@Service
public class ShopService {

    @Autowired
    private ShopMapper mapper;
    @Autowired
    private ZBTProperties properties;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    public Page<ShopDTO> pagination(SearchShopCondition condition) {
        return mapper.pagination(condition.getPage(), condition);
    }

    @Transactional
    public void insert(Shop shop) {
        if (mapper.get(shop.getGiftProductId()) != null) {
            throw new BusinessException(ExceptionCode.SHOP_EXISTS);
        }
        getStock(shop);
        shop.setCt(new Date());
        mapper.insert(shop);
        if (shop.getStatus() != null && shop.getStock() > 0) {
            redisTemplateFacde.set("SHOP_" + shop.getGiftProductId(), shop.getStock().toString());
        }
    }

    private void getStock(Shop shop) {
        GiftProductPlus giftProductPlus = giftProductPlusMapper.selectById(shop.getGiftProductId());
        Map<String, Object> map = Maps.newHashMap();
        map.put("app-key", properties.getAppKey());
        map.put("appId", properties.getAppId());
        map.put("keyword", giftProductPlus.getName());
        String resultJson = HttpUtil2.doGet(properties.getSearchUrl(), map);
        ZBTResult objectMap = JSON.fromJSON(resultJson, ZBTResult.class);

        if (objectMap.getSuccess()) {
            PriceInfo priceInfo = objectMap.getData().getList().get(0).getPriceInfo();
            if (null != priceInfo) {
                shop.setStock(priceInfo.getQuantity() / 10);
            }
        }
    }

    @Transactional
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            Shop shop = get(id);
            if (StringUtils.hasText(redisTemplateFacde.get("SHOP_" + shop.getGiftProductId()))) {
                redisTemplateFacde.delete("SHOP_" + shop.getGiftProductId());
            }
            mapper.deleteById(id);
        }
    }

    public Shop get(int id) {
        return mapper.selectById(id);
    }

    public Shop getByProductId(int giftProductId) {
        return mapper.get(giftProductId);
    }

    @Transactional
    public void update(Shop shop) {
        shop.setUt(new Date());
        mapper.updateById(shop);
        if (shop.getStock() > 0) {
            redisTemplateFacde.set("SHOP_" + shop.getGiftProductId(), shop.getStock().toString());
        }
    }

    @Transactional
    public void addBatch(List<Shop> shops) {
        shops.forEach(this::insert);
    }

    @Transactional
    public void updateBatch() {
        mapper.find(ShopStatus.NORMAL).forEach(shop -> {
            getStock(shop);
            update(shop);
        });
    }
}
