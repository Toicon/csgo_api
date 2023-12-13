package com.csgo.mapper.plus.gift;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.gift.SearchGiftProductDTOCondition;
import com.csgo.condition.shop.SearchProductCondition;
import com.csgo.condition.shop.SearchShopProductCondition;
import com.csgo.domain.enums.GiftProductStatusEnum;
import com.csgo.domain.plus.gift.GiftProductDTO;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.gift.MineGiftProductDTO;
import com.csgo.domain.plus.gift.RandomGiftProductDTO;
import com.csgo.domain.plus.shop.ShopGiftProductDTO;
import com.csgo.modular.product.model.front.ProductSimpleVO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface GiftProductPlusMapper extends BaseMapper<GiftProductPlus> {

    Page<ShopGiftProductDTO> pagination(IPage<ShopGiftProductDTO> page, SearchShopProductCondition condition);

    Page<GiftProductDTO> paginationSummary(IPage<GiftProductDTO> page, SearchGiftProductDTOCondition condition);

    default Page<GiftProductPlus> pagination(Page<GiftProductPlus> page, SearchProductCondition condition) {
        LambdaQueryWrapper<GiftProductPlus> wrapper = new LambdaQueryWrapper<>();
        if (null != condition.getKeywords()) {
            wrapper.like(GiftProductPlus::getName, condition.getKeywords());
        }
        wrapper.eq(GiftProductPlus::getStatus, GiftProductStatusEnum.NORMAL);
        return selectPage(page, wrapper);
    }

    default List<GiftProductPlus> findByGiftProductIds(Collection<Integer> giftProductIds) {
        LambdaQueryWrapper<GiftProductPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GiftProductPlus::getId, giftProductIds);
        wrapper.eq(GiftProductPlus::getStatus, GiftProductStatusEnum.NORMAL);
        return selectList(wrapper);
    }

    default int totalCount(String name) {
        LambdaQueryWrapper<GiftProductPlus> wrapper = new LambdaQueryWrapper<>();
        if (null != name) {
            wrapper.like(GiftProductPlus::getName, name);
        }
        wrapper.eq(GiftProductPlus::getStatus, GiftProductStatusEnum.NORMAL);
        return selectCount(wrapper);
    }

    default List<GiftProductPlus> findByGiftId(int giftId) {
        LambdaQueryWrapper<GiftProductPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GiftProductPlus::getGiftId, giftId);
        wrapper.eq(GiftProductPlus::getStatus, GiftProductStatusEnum.NORMAL);
        return selectList(wrapper);
    }

    default List<GiftProductPlus> findByName(String name) {
        LambdaQueryWrapper<GiftProductPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(GiftProductPlus::getName, name);
        wrapper.eq(GiftProductPlus::getStatus, GiftProductStatusEnum.NORMAL);
        return selectList(wrapper);
    }

    /**
     * 根据金额返回最接近商品记录
     *
     * @param price
     * @return
     */
    MineGiftProductDTO getGiftProductByPrice(BigDecimal price);

    /**
     * 根据饰品id批量获取饰品列表
     *
     * @param idList
     * @return
     */
    default List<GiftProductPlus> findByIds(List<Integer> idList) {
        LambdaQueryWrapper<GiftProductPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GiftProductPlus::getId, idList);
        return selectList(wrapper);
    }

    /**
     * 根据指定金额返回最接近商品记录（随机饰品）
     *
     * @param price
     * @return
     */
    RandomGiftProductDTO getRandomGiftProductByPrice(BigDecimal price);

    ProductSimpleVO getProductByNearPrice(BigDecimal price);

}
