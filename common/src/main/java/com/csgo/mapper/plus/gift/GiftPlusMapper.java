package com.csgo.mapper.plus.gift;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.gift.SearchGiftCondition;
import com.csgo.domain.enums.ProbabilityTypeEnum;
import com.csgo.domain.plus.gift.GiftPlus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface GiftPlusMapper extends BaseMapper<GiftPlus> {

    default Page<GiftPlus> pagination(SearchGiftCondition condition) {
        LambdaQueryWrapper<GiftPlus> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getName())) {
            wrapper.like(GiftPlus::getName, condition.getName());
        }
        if (null != condition.getTypeId()) {
            wrapper.eq(GiftPlus::getTypeId, condition.getTypeId());
        }
        if (condition.isMembershipGrade()) {
            wrapper.isNotNull(GiftPlus::getMembershipGrade);
        } else {
            wrapper.isNull(GiftPlus::getMembershipGrade);
        }
        wrapper.orderByAsc(GiftPlus::getPrice);
        wrapper.orderByAsc(GiftPlus::getId);
        return selectPage(condition.getPage(), wrapper);
    }

    default List<GiftPlus> findByIds(Collection<Integer> giftIds) {
        LambdaQueryWrapper<GiftPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GiftPlus::getId, giftIds);
        return selectList(wrapper);
    }

    default List<GiftPlus> findByTypeId(int typeId) {
        LambdaQueryWrapper<GiftPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GiftPlus::getTypeId, typeId);
        return selectList(wrapper);
    }

    default GiftPlus findByMembershipGrade(Integer membershipGrade) {
        LambdaQueryWrapper<GiftPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GiftPlus::getMembershipGrade, membershipGrade);
        return selectOne(wrapper);
    }

    default List<GiftPlus> findLeGrade(Integer membershipGrade) {
        LambdaQueryWrapper<GiftPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(GiftPlus::getMembershipGrade);
        wrapper.le(GiftPlus::getMembershipGrade, membershipGrade);
        wrapper.orderByAsc(GiftPlus::getMembershipGrade);
        return selectList(wrapper);
    }

    default List<GiftPlus> findAllMembership() {
        LambdaQueryWrapper<GiftPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(GiftPlus::getMembershipGrade);
        wrapper.orderByAsc(GiftPlus::getMembershipGrade);
        return selectList(wrapper);
    }

    default List<GiftPlus> findProbabilityGiftList() {
        LambdaQueryWrapper<GiftPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GiftPlus::getProbabilityType, ProbabilityTypeEnum.PRICE.getCode());
        return selectList(wrapper);
    }
}
