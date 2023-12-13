package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.gift.SearchGiftProductDTOCondition;
import com.csgo.condition.shop.SearchProductCondition;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.enums.GiftProductStatusEnum;
import com.csgo.domain.plus.accessory.LuckyProduct;
import com.csgo.domain.plus.accessory.RandomProduct;
import com.csgo.domain.plus.gift.GiftProductDTO;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.job.IgProductJob;
import com.csgo.mapper.GiftProductMapper;
import com.csgo.mapper.plus.accessory.LuckyProductPlusMapper;
import com.csgo.mapper.plus.accessory.RandomLuckyProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductRecordPlusMapper;
import com.csgo.modular.product.enums.ProductCsgoTypeEnums;
import com.csgo.modular.product.enums.ProductKindEnums;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class GiftProductService {

    @Autowired
    private GiftProductMapper giftProductMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;
    @Autowired
    private GiftProductRecordPlusMapper giftProductRecordPlusMapper;
    @Autowired
    private LuckyProductPlusMapper luckyProductPlusMapper;
    @Autowired
    private RandomLuckyProductPlusMapper randomProductMapper;

    @Autowired
    private IgProductJob igProductJob;

    public Page<GiftProductPlus> pagination(SearchProductCondition condition) {
        return giftProductPlusMapper.pagination(condition.getPage(), condition);
    }

    public Page<GiftProductDTO> paginationSummary(SearchGiftProductDTOCondition condition) {
        return giftProductPlusMapper.paginationSummary(condition.getPage(), condition);
    }

    public PageInfo<GiftProduct> pageProductList(Integer pageNum, Integer pageSize, String keywords, Integer isLuckyProduct) {
        Page<GiftProduct> page = new Page<>(pageNum, pageSize);
        Page<GiftProduct> giftProductList = giftProductMapper.pageProductList(page, keywords, isLuckyProduct);
        return new PageInfo<>(giftProductList);
    }

    public int add(GiftProduct gift) {
        if (ProductCsgoTypeEnums.GIFT_KEY.getType().equals(gift.getCsgoType())) {
            gift.setProductKind(ProductKindEnums.GIFT_KEY.getCode());
        }
        return giftProductMapper.insert(gift);
    }

    public GiftProduct queryById(int id) {
        return giftProductMapper.selectByPrimaryKey(id);
    }

    public int update(GiftProduct gift, int id) {
        gift.setId(id);
        if (ProductCsgoTypeEnums.GIFT_KEY.getType().equals(gift.getCsgoType())) {
            gift.setProductKind(ProductKindEnums.GIFT_KEY.getCode());
        }
        return giftProductMapper.updateByPrimaryKey(gift);
    }

    public List<GiftProduct> getList(GiftProduct giftProduct) {
        return giftProductMapper.getList(giftProduct);
    }

    public int totalCount(String name) {
        return giftProductPlusMapper.totalCount(name);
    }

    public List<GiftProduct> getListLt(GiftProduct giftProduct) {
        return giftProductMapper.getListLt(giftProduct);
    }

    public int delete(int id) {
        return giftProductMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public int addPlus(GiftProductPlus giftProductPlus) {
        if (ProductCsgoTypeEnums.GIFT_KEY.getType().equals(giftProductPlus.getCsgoType())) {
            giftProductPlus.setProductKind(ProductKindEnums.GIFT_KEY.getCode());
        }
        giftProductPlusMapper.insert(giftProductPlus);
        return giftProductPlus.getId();
    }

    public GiftProductPlus get(int id) {
        return giftProductPlusMapper.selectById(id);
    }

    @Async
    public void updateProps() {
        // igProductJob.syncProductByIG();
        igProductJob.syncProductByZbt();
        // yyProductJob.syncProductByYy();
    }

    @Transactional
    public void updateStatus(int id, GiftProductStatusEnum status) {
        GiftProductPlus giftProductPlus = get(id);
        giftProductPlus.setStatus(status);
        giftProductPlus.setUpdatedAt(new Date());
        giftProductPlusMapper.updateById(giftProductPlus);
    }

    public List<GiftProductPlus> findByName(String name) {
        return giftProductPlusMapper.findByName(name);
    }

    @Transactional
    public void updateRelate(Integer originalId, String name) {
        List<GiftProductPlus> giftProductPlus = giftProductPlusMapper.findByName(name);
        if (CollectionUtils.isEmpty(giftProductPlus)) {
            return;
        }
        GiftProductPlus productPlus = giftProductPlus.get(0);

        List<GiftProductRecordPlus> giftProductRecordPluses = giftProductRecordPlusMapper.findByGiftProductIds(Collections.singleton(originalId), null);
        if (!CollectionUtils.isEmpty(giftProductRecordPluses)) {
            giftProductRecordPluses.forEach(record -> {
                record.setGiftProductId(productPlus.getId());
                record.setUt(new Date());
                giftProductRecordPlusMapper.updateById(record);
            });
        }

        List<LuckyProduct> luckyProducts = luckyProductPlusMapper.findByGiftProductId(originalId);
        if (!CollectionUtils.isEmpty(luckyProducts)) {
            luckyProducts.forEach(luckyProduct -> {
                luckyProduct.setPrice(productPlus.getPrice());
                luckyProduct.setGiftProductId(productPlus.getId());
                luckyProductPlusMapper.updateById(luckyProduct);
            });
        }

        List<RandomProduct> randomLuckyProducts = randomProductMapper.findByGiftProductId(originalId);
        if (!CollectionUtils.isEmpty(randomLuckyProducts)) {
            luckyProducts.forEach(random -> {
                random.setPrice(productPlus.getPrice());
                random.setGiftProductId(productPlus.getId());
                luckyProductPlusMapper.updateById(random);
            });
        }
    }

    @Transactional
    public void updateOff(int id) {
        List<GiftProductRecordPlus> giftProductRecordPluses = giftProductRecordPlusMapper.findByGiftProductIds(Collections.singleton(id), null);
        if (!CollectionUtils.isEmpty(giftProductRecordPluses)) {
            giftProductRecordPluses.forEach(record -> giftProductRecordPlusMapper.deleteById(record.getId()));
        }
    }

    /**
     * 获取是否特殊金配置数量
     *
     * @param id
     * @return
     */
    public int getCountSpecialStateByGiftId(int id) {
        return giftProductRecordPlusMapper.getCountSpecialStateByGiftId(id);
    }

    /**
     * 价格权重礼包物品更新
     */
    @Async
    public void updateGiftProductByPrice() {
        igProductJob.syncUpdateGiftProductByPrice();
    }

}
