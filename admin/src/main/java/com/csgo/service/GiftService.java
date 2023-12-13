package com.csgo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.framework.exception.BizException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.modular.product.enums.ProductCsgoTypeEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.gift.SearchGiftCondition;
import com.csgo.domain.Gift;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.mapper.GiftMapper;
import com.csgo.mapper.plus.gift.GiftPlusMapper;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;

@Service
public class GiftService {

    @Autowired
    private GiftMapper giftMapper;
    @Autowired
    private GiftPlusMapper giftPlusMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;

    public Gift queryGiftId(int id) {
        return giftMapper.selectByPrimaryKey(id);
    }

    public List<Gift> getList(Gift gift) {
        return giftMapper.getList(gift);
    }

    public int delete(int id) {
        return giftMapper.deleteByPrimaryKey(id);
    }

    public List<GiftPlus> findByIds(Collection<Integer> giftIds) {
        if (CollectionUtils.isEmpty(giftIds)) {
            return new ArrayList<>();
        }
        return giftPlusMapper.findByIds(giftIds);
    }

    public List<GiftPlus> findByTypeId(int id) {
        return giftPlusMapper.findByTypeId(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insert(GiftPlus gift) {
        checkKeyProduct(gift);
        gift.setCeatedAt(new Date());
        if (gift.getMembershipGrade() != null) {
            GiftPlus exists = giftPlusMapper.findByMembershipGrade(gift.getMembershipGrade());
            if (null != exists) {
                throw new BusinessException(ExceptionCode.MEMBERSHIP_BOX_CONFIG_ERROR);
            }
        }
        giftPlusMapper.insert(gift);
    }

    private void checkKeyProduct(GiftPlus gift) {
        if (!"碎片专区".equals(gift.getType())) {
            return;
        }

        if (gift.getKeyNum() == null) {
            throw BizException.of("碎片数量不能为空");
        }

        if (gift.getKeyNum() <= 0) {
            throw BizException.of("碎片数量必须大于零");
        }

        if (gift.getKeyProductId() != null) {
            GiftProductPlus product = giftProductPlusMapper.selectById(gift.getKeyProductId());
            if (product == null) {
                throw BizServerException.of(CommonBizCode.PRODUCT_NOT_FOUND);
            }
            if (!ProductCsgoTypeEnums.GIFT_KEY.getType().equals(product.getCsgoType())) {
                throw BizException.of("非碎片饰品，请重新选择");
            }
        }
    }

    public GiftPlus getPlus(int id) {
        return giftPlusMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePlus(GiftPlus gift) {
        checkKeyProduct(gift);
        if (gift.getMembershipGrade() != null) {
            GiftPlus exists = giftPlusMapper.findByMembershipGrade(gift.getMembershipGrade());
            if (null != exists && !exists.getId().equals(gift.getId())) {
                throw new BusinessException(ExceptionCode.MEMBERSHIP_BOX_CONFIG_ERROR);
            }
        }
        gift.setUpdatedAt(new Date());
        giftPlusMapper.updateById(gift);
    }

    public Page<GiftPlus> pagination(SearchGiftCondition condition) {
        return giftPlusMapper.pagination(condition);
    }

    @Transactional
    public void updateSwitch(int id) {
        GiftPlus giftPlus = giftPlusMapper.selectById(id);
        giftPlus.setHidden(!giftPlus.getHidden());
        giftPlusMapper.updateById(giftPlus);
    }

    public GiftPlus get(int id) {
        return giftPlusMapper.selectById(id);
    }
}
