package com.csgo.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.ExchangeRate;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.domain.plus.gift.RandomGiftProductDTO;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.mapper.ExchangeRateMapper;
import com.csgo.mapper.plus.accessory.LuckyProductPlusMapper;
import com.csgo.mapper.plus.accessory.RandomLuckyProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductRecordPlusMapper;
import com.csgo.mapper.plus.shop.ShopMapper;
import com.csgo.modular.common.sms.logic.SmsLogic;
import com.csgo.modular.ig.config.IgProperties;
import com.csgo.modular.product.enums.ProductSourceTypeEnums;
import com.csgo.modular.product.logic.GiftProductLogic;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.service.smm.AliSmsService;
import com.csgo.support.ZBT.PriceInfo;
import com.csgo.support.ZBT.ZBTBean;
import com.csgo.support.ZBT.ZBTDate;
import com.csgo.support.ZBT.ZBTResult;
import com.csgo.support.ig.IgProduct;
import com.csgo.util.HttpUtil2;
import com.csgo.util.HttpsUtil2;
import com.echo.framework.support.jackson.json.JSON;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SpringScheduled
 * @description:
 * @author: Andy
 * @time: 2020/11/9 10:10
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IgProductJob {
    @Autowired
    private ZBTProperties properties;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;
    @Autowired
    private ExchangeRateMapper exchangeRateMapper;
    @Autowired
    private GiftProductRecordPlusMapper giftProductRecordPlusMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private RandomLuckyProductPlusMapper randomLuckyProductPlusMapper;
    @Autowired
    private LuckyProductPlusMapper luckyProductPlusMapper;
    @Autowired
    private AliSmsService aliSmsService;
    @Autowired
    private IgProperties igProperties;
    @Autowired
    private GiftPlusMapper giftPlusMapper;

    private final SmsLogic smsLogic;

    private final GiftProductLogic giftProductLogic;

    /**
     * IG获取商品信息
     */
    public void syncProductByIG() {
        log.info("[IG同步开始]");
        Map<String, GiftProductPlus> giftProductPlusMap = giftProductPlusMapper.selectList(new LambdaQueryWrapper<GiftProductPlus>().isNotNull(GiftProductPlus::getItemId)).stream()
                .collect(Collectors.toMap(GiftProductPlus::getItemId, giftProductPlus -> giftProductPlus, (map, map1) -> map));

        ExchangeRate rate = exchangeRateMapper.getById(1);
        Set<Integer> useIds = null; // findAllUseProductId();
        for (int i = 1; i < 200; i++) {
            boolean isMore = updateByIg(i, giftProductPlusMap, rate, useIds);
            if (!isMore) {
                break;
            }
        }

        log.info("[IG同步结束]");
        smsLogic.sendGlobalUpdate(properties.getNoticePhone());
    }

    private boolean updateByIg(int pageNo, Map<String, GiftProductPlus> giftProductPlusMap, ExchangeRate rate, Set<Integer> useIds) {
        Map<String, String> params = new HashMap<>();
        params.put("partner_key", igProperties.getPartnerKey());
        params.put("public_key", igProperties.getPublicKey());
        String signParams = igProperties.getPartnerKey() + igProperties.getPublicKey() + igProperties.getSecretKey();
        params.put("sign", DigestUtils.md5Hex(signParams.getBytes(StandardCharsets.UTF_8)));
        params.put("app_id", igProperties.getAppId());
        params.put("page_no", String.valueOf(pageNo));
        params.put("page_size", "100");
        log.info("IG拉取价格请求参数：{}", JSON.toJSON(params));
        String igQueryResult = HttpsUtil2.sendFormPost(igProperties.getQueryPriceUrl(), params);
        // log.info("IG拉取价格响应结果：{}", igQueryResult);
        Map<String, Object> jsonObject = JSON.fromJSON(igQueryResult, Map.class);
        int code = (int) jsonObject.get("code");
        if (code != 1) {
            log.info("IG拉取价格响应结果异常，当前页数：{}", pageNo);
            return false;
        }

        Map<String, Object> data = (Map<String, Object>) jsonObject.get("data");
        List<Map<String, Object>> products = (List<Map<String, Object>>) data.get("products");
        if (null == products || products.size() == 0) {
            log.info("IG拉取价格响应结果异常，当前页数：{}", pageNo);
            return false;
        }
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> product = products.get(i);
            String json = JSON.toJSON(product);
            IgProduct igProduct = com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject.parseObject(json, IgProduct.class);
            if (igProduct.getMinPrice().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            GiftProductPlus gp = giftProductPlusMap.get(String.valueOf(igProduct.getProductId()));
            if (null == gp) {
                GiftProductPlus newProduct = giftProductLogic.createIgProduct(rate, igProduct);
                giftProductPlusMap.put(newProduct.getItemId(), newProduct);
            } else {
                giftProductLogic.updateIgProduct(igProduct, gp, rate, useIds);
            }
        }
        Map<String, Object> page = (Map<String, Object>) data.get("page");
        if ((int) page.get("is_more") != 1) {
            return false;
        }
        return true;
    }

    public void syncProductByZbt() {
        log.info("[Zbt开始同步]");
        List<String> keys = zbtProductFiltersService.getRarityList();
        if (CollectionUtils.isEmpty(keys)) {
            log.error("定时更新价格，获取品质类目信息失败,类目信息为空");
            return;
        }

        LambdaQueryWrapper<GiftProductPlus> wrapper = new LambdaQueryWrapper<GiftProductPlus>().isNotNull(GiftProductPlus::getEnglishName);
        Map<String, List<GiftProductPlus>> giftProductPlusMap = giftProductPlusMapper.selectList(wrapper).stream()
                .collect(Collectors.groupingBy(GiftProductPlus::getEnglishName));

        ExchangeRate rate = exchangeRateMapper.getById(1);
        Set<Integer> useIds = null;
        for (String key : keys) {
            updateByZbt(key, rate, giftProductPlusMap, useIds);
        }
        log.info("[Zbt同步结束]");
        smsLogic.sendGlobalUpdate(properties.getNoticePhone());
    }

    private void updateByZbt(String key, ExchangeRate exchangeRate, Map<String, List<GiftProductPlus>> giftProductPlusMap, Set<Integer> useIds) {
        int bbb = 100;
        Map<String, Object> map = Maps.newHashMap();
        map.put("app-key", properties.getAppKey());
        map.put("appId", properties.getAppId());
        map.put("rarity", key);
        map.put("limit", 100);
        map.put("orderBy", 0);

        log.info("更新价格exchangeRate >>{}", exchangeRate);
        for (int i = 1; i < bbb; i++) {
            map.put("page", i);
            //由于zbt每次只能请求100数量，所以分批次请求
            String string = HttpUtil2.doGet(properties.getSearchUrl(), map);
            ZBTResult objectMap = new ZBTResult();
            objectMap = JSON.fromJSON(string, objectMap.getClass());
            if (objectMap.getSuccess() == null || !objectMap.getSuccess()) {
                continue;
            }
            ZBTDate data2 = objectMap.getData();
            if (data2 == null) {
                continue;
            }
            if (i == 1) {
                bbb = data2.getTotal() / 100;
                bbb += 1;
            }
            if (CollectionUtils.isEmpty(data2.getList())) {
                continue;
            }
            List<ZBTBean> dlist = data2.getList();
            for (ZBTBean zbt : dlist) {
                try {
                    PriceInfo priceInfo = zbt.getPriceInfo();
                    List<GiftProductPlus> list = giftProductPlusMap.get(zbt.getMarketHashName());

                    if (null == priceInfo.getPrice() || BigDecimalUtil.lessEqualZero(priceInfo.getPrice())) {
                        log.error("[同步zbt] price:{} priceInfo:{}", priceInfo.getPrice(), JSON.toJSON(priceInfo));
                        continue;
                    }

                    if (null != list) {
                        for (GiftProductPlus gp : list) {
                            zbtProductFiltersService.updateProductPrice(priceInfo, gp, zbt, exchangeRate, useIds);
                        }
                    }
//                    else {
//                        GiftProductPlus gp = new GiftProductPlus();
//                        if (StringUtils.hasText(exchangeRate.getUpsAndDowns())) {
//                            BigDecimal rate = new BigDecimal(exchangeRate.getUpsAndDowns());
//                            gp.setPrice(priceInfo.getPrice().add(priceInfo.getPrice().multiply(rate.divide(BigDecimal.valueOf(100)))).setScale(2, BigDecimal.ROUND_HALF_UP));
//                        } else {
//                            gp.setPrice(priceInfo.getPrice());
//                        }
//                        gp.setOriginAmount(priceInfo.getPrice());
//                        gp.setZbtStock(priceInfo.getQuantity());
//                        gp.setName(zbt.getItemName());
//                        gp.setImg(zbt.getImageUrl());
//                        gp.setZbtItemId(zbt.getItemId());
//                        gp.setEnglishName(zbt.getMarketHashName());
//                        gp.setCreatedAt(new Date());
//                        gp.setGrade(zbt.getRarityColor());
//                        gp.setCsgoType(zbt.getType());
//                        gp.setExteriorName(zbt.getExteriorName());
//                        gp.setSourceType(ProductSourceTypeEnums.ZBT.getCode());
//                        zbtProductFiltersService.insertProduct(gp);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Set<Integer> findAllUseProductId() {
        Set<Integer> set = new HashSet<>();
/*        List<Integer> blindIds = blindBoxProductMapper.findAllProductId();
        if (!CollectionUtils.isEmpty(blindIds)) {
            set.addAll(blindIds);
        }*/
        List<Integer> giftIds = giftProductRecordPlusMapper.findAllProductId();
        if (!CollectionUtils.isEmpty(giftIds)) {
            set.addAll(giftIds);
        }
        List<Integer> shopIds = shopMapper.findAllProductId();
        if (!CollectionUtils.isEmpty(shopIds)) {
            set.addAll(shopIds);
        }
        List<Integer> luckyIds = luckyProductPlusMapper.findAllProductId();
        if (!CollectionUtils.isEmpty(luckyIds)) {
            set.addAll(luckyIds);
        }
        List<Integer> randomIds = randomLuckyProductPlusMapper.findAllProductId();
        if (!CollectionUtils.isEmpty(randomIds)) {
            set.addAll(randomIds);
        }
        return set;
    }


    /**
     * 价格权重礼包物品更新
     */
    @Transactional
    public void syncUpdateGiftProductByPrice() {
        log.info("[价格权重礼包物品更新开始]");
        List<GiftPlus> giftList = giftPlusMapper.findProbabilityGiftList();
        if (CollectionUtils.isEmpty(giftList)) {
            log.info("获取价格权重礼包列表为空");
            return;
        }
        log.info("开始执行价格权重礼包物品更新");
        giftList.forEach(gift -> {
            this.updateGiftProductByPrice(gift.getId());
        });
        log.info("[价格权重礼包物品更新结束]");
        smsLogic.sendGlobalUpdate(properties.getNoticePhone());
    }

    /**
     * 修改价格权重物品信息
     *
     * @param giftId
     */
    public void updateGiftProductByPrice(Integer giftId) {
        if (giftId == null) {
            return;
        }
        List<GiftProductRecordPlus> productRecordPlusList = giftProductRecordPlusMapper.findByGiftId(giftId);
        if (!CollectionUtils.isEmpty(productRecordPlusList)) {
            for (GiftProductRecordPlus item : productRecordPlusList) {
                //价格权重:根据价格获取商品id
                RandomGiftProductDTO randomGiftProductDTO = giftProductPlusMapper.getRandomGiftProductByPrice(item.getProbabilityPrice());
                if (randomGiftProductDTO == null) {
                    log.error("获取不到礼包id：{}，价格：{}", giftId, item.getProbabilityPrice());
                    continue;
                }
                Integer giftProductId = randomGiftProductDTO.getGiftProductId();
                item.setGiftProductId(giftProductId);
                item.setUt(new Date());
                giftProductRecordPlusMapper.updateById(item);
            }
        }

    }
}
