package com.csgo.mapper.plus.roll;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.SearchRollGiftPlusCondition;
import com.csgo.domain.plus.roll.RollGiftPlus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface RollGiftPlusMapper extends BaseMapper<RollGiftPlus> {

    default Page<RollGiftPlus> pagination(SearchRollGiftPlusCondition condition) {
        LambdaQueryWrapper<RollGiftPlus> wrapper = Wrappers.lambdaQuery();
        if (null != condition.getRollId()) {
            wrapper.eq(RollGiftPlus::getRollId, condition.getRollId());
        }
        wrapper.orderByDesc(RollGiftPlus::getPrice);
        wrapper.orderByDesc(RollGiftPlus::getId);
        return selectPage(condition.getPage(), wrapper);
    }

    default List<RollGiftPlus> findByRollIds(Collection<Integer> rollIds) {
        LambdaQueryWrapper<RollGiftPlus> wrapper = Wrappers.lambdaQuery();
        if (!CollectionUtils.isEmpty(rollIds)) {
            wrapper.in(RollGiftPlus::getRollId, rollIds);
        }
        return selectList(wrapper);
    }

    default List<RollGiftPlus> find(Integer rollId) {
        LambdaQueryWrapper<RollGiftPlus> wrapper = Wrappers.lambdaQuery();
        if (null != rollId) {
            wrapper.eq(RollGiftPlus::getRollId, rollId);
        }
        return selectList(wrapper);
    }

    default void updateByGiftProductId(Integer giftProductId, BigDecimal price, String img, String productName, String grade) {
        LambdaUpdateWrapper<RollGiftPlus> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(RollGiftPlus::getGiftProductId, giftProductId);
        wrapper.set(RollGiftPlus::getPrice, price);
        wrapper.set(RollGiftPlus::getImg, img);
        wrapper.set(RollGiftPlus::getProductname, productName);
        wrapper.set(RollGiftPlus::getGrade, grade);
        update(null, wrapper);
    }
}
