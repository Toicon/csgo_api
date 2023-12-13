package com.csgo.modular.product.logic;

import cn.hutool.core.util.NumberUtil;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.ExchangeRate;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.GiftProductUpdateRecord;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductUpdateRecordMapper;
import com.csgo.modular.product.enums.ProductSourceTypeEnums;
import com.csgo.support.ig.IgProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Set;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiftProductLogic {

    private final GiftProductPlusMapper giftProductPlusMapper;
    private final GiftProductUpdateRecordMapper giftProductUpdateRecordMapper;

    private final GiftProductSyncLogic giftProductSyncLogic;

    private final static BigDecimal LOW_PRICE = new BigDecimal("0.01");
    private final static BigDecimal PRICE_RATE = new BigDecimal("6.5");

    @Transactional(rollbackFor = Exception.class)
    public GiftProductPlus createIgProduct(ExchangeRate exchangeRate, IgProduct igProduct) {
        BigDecimal minSellPrice = getIgMinPrice(igProduct);

        BigDecimal price = calcPrice(exchangeRate, minSellPrice);
        String colour = getColour(price);

        GiftProductPlus gp = new GiftProductPlus();
        gp.setRmbPrice(igProduct.getMinPrice());
        gp.setPrice(price);
        gp.setOriginAmount(minSellPrice);

        gp.setZbtStock(igProduct.getSaleCount());
        gp.setCsgoType(igProduct.getCtgName());

        gp.setName(getIgName(igProduct.getExteriorName(), igProduct.getName()));
        gp.setOriginName(igProduct.getName());
        gp.setImg(igProduct.getIconUrl());

        gp.setEnglishName(igProduct.getMarket_hash_name());
        gp.setExteriorName(igProduct.getExteriorName());
        gp.setGrade(colour);

        gp.setItemId(String.valueOf(igProduct.getProductId()));
        gp.setSourceType(ProductSourceTypeEnums.IG.getCode());
        gp.setCreatedAt(new Date());
        gp.setUpdatedAt(new Date());
        giftProductPlusMapper.insert(gp);
        return gp;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateIgProduct(IgProduct igProduct, GiftProductPlus gp, ExchangeRate exchangeRate, Set<Integer> useIds) {
        BigDecimal minSellPrice = getIgMinPrice(igProduct);
        BigDecimal price = calcPrice(exchangeRate, minSellPrice);
        String colour = getColour(price);

        BigDecimal nowPrice = gp.getPrice();
        BigDecimal nowOriginPrice = gp.getOriginAmount();

        if (BigDecimalUtil.greaterThanZero(nowOriginPrice)) {
            BigDecimal scale = (minSellPrice.subtract(nowOriginPrice)).divide(nowOriginPrice, 2, RoundingMode.DOWN);
            if (canUpdatePrice(price, scale)) {
                gp.setOriginAmount(minSellPrice);
                gp.setRmbPrice(igProduct.getMinPrice());
                gp.setPrice(price);

                giftProductSyncLogic.syncRelation(gp);
            } else {
                GiftProductUpdateRecord record = new GiftProductUpdateRecord();
                record.setEnglishName(igProduct.getMarket_hash_name());
                record.setName(getIgName(igProduct.getExteriorName(), igProduct.getName()));
                record.setImg(igProduct.getIconUrl());
                record.setNowOriginPrice(nowOriginPrice);
                record.setNowPrice(nowPrice);
                record.setProductId(gp.getId());
                record.setUpdateOriginPrice(minSellPrice);
                record.setUpdatePrice(price);
                BaseEntity.created(record, "全局拉取");
                giftProductUpdateRecordMapper.insert(record);
            }
        } else {
            log.error("[更新价格] 价格错误 原价：{}", nowOriginPrice);
        }

        gp.setZbtStock(igProduct.getSaleCount());
        gp.setCsgoType(igProduct.getCtgName());

        gp.setName(getIgName(igProduct.getExteriorName(), igProduct.getName()));
        gp.setOriginName(igProduct.getName());
        gp.setImg(igProduct.getIconUrl());

        gp.setEnglishName(igProduct.getMarket_hash_name());
        gp.setExteriorName(igProduct.getExteriorName());
        gp.setGrade(colour);

        gp.setItemId(String.valueOf(igProduct.getProductId()));
        gp.setUpdatedAt(new Date());
        giftProductPlusMapper.updateById(gp);
    }

    private BigDecimal getIgMinPrice(IgProduct igProduct) {
        BigDecimal price = igProduct.getMinPrice().divide(PRICE_RATE, 2, RoundingMode.HALF_UP);
        if (BigDecimalUtil.lessThan(price, LOW_PRICE)) {
            return LOW_PRICE;
        }
        return price;
    }

    private BigDecimal calcPrice(ExchangeRate exchangeRate, BigDecimal minSellPrice) {
        BigDecimal price = minSellPrice;
        if (NumberUtil.isNumber(exchangeRate.getUpsAndDowns())) {
            BigDecimal rate = new BigDecimal(exchangeRate.getUpsAndDowns());

            BigDecimal addPrice = minSellPrice.multiply(rate).divide(BigDecimal.valueOf(100));
            price = minSellPrice.add(addPrice).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }

    private String wrapName(String name, String exteriorName) {
        if (name.contains("(")) {
            return name;
        }
        return getIgName(exteriorName, name);
    }

    private static String getIgName(String product, String product1) {
        return StringUtils.hasText(product) ? product1 + " (" + product + ")" : product1;
    }

    public String getColour(BigDecimal price) {
        if (price.compareTo(new BigDecimal("19.99")) <= 0) {
            return "#4b69ff";
        }
        if (price.compareTo(new BigDecimal(20)) >= 0 && price.compareTo(new BigDecimal("99.99")) <= 0) {
            return "#eb4b4b";
        }
        return "#e4ae39";
    }

    private boolean canUpdatePrice(BigDecimal price, BigDecimal scale) {
        return price.compareTo(new BigDecimal(5)) < 0 || scale.compareTo(new BigDecimal("0.3")) < 0;
    }

}
