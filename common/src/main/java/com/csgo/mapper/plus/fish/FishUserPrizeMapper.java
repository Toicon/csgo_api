package com.csgo.mapper.plus.fish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.fish.SearchFishMyUserPrizeCondition;
import com.csgo.condition.fish.SearchFishPriceCondition;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.fish.FishUserPrize;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 钓鱼用户获得奖励
 */
@Repository
public interface FishUserPrizeMapper extends BaseMapper<FishUserPrize> {

    default Page<FishUserPrize> pagination(SearchFishPriceCondition condition) {
        LambdaQueryWrapper<FishUserPrize> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getUserName())) {
            wrapper.like(FishUserPrize::getUserName, condition.getUserName());
        }
        wrapper.orderByDesc(FishUserPrize::getCreateDate);
        return selectPage(condition.getPage(), wrapper);
    }

    /**
     * 最近挂机奖励结果
     *
     * @param giftId
     * @return
     */
    default List<FishUserPrize> findPrizeResult(Integer userId, Integer giftId) {
        LambdaQueryWrapper<FishUserPrize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FishUserPrize::getUserId, userId);
        wrapper.eq(FishUserPrize::getGiftId, giftId);
        wrapper.eq(FishUserPrize::getPromptState, YesOrNoEnum.NO.getCode());
        wrapper.orderByDesc(FishUserPrize::getId);
        return selectList(wrapper);
    }

    /**
     * 最近掉落
     *
     * @param giftId
     * @return
     */
    default List<FishUserPrize> findLastFall(Integer giftId) {
        LambdaQueryWrapper<FishUserPrize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FishUserPrize::getGiftId, giftId);
        wrapper.orderByDesc(FishUserPrize::getId);
        wrapper.last(" limit 20");
        return selectList(wrapper);
    }

    /**
     * 活动未结束
     *
     * @param giftId
     * @param userId
     * @param turn
     * @return
     */
    default FishUserPrize getTimeGiveUp(Integer giftId, Integer userId, Integer activityId, Integer turn) {
        LambdaQueryWrapper<FishUserPrize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FishUserPrize::getGiftId, giftId);
        wrapper.eq(FishUserPrize::getActivityId, activityId);
        wrapper.eq(FishUserPrize::getUserId, userId);
        wrapper.eq(FishUserPrize::getTurn, turn);
        wrapper.orderByDesc(FishUserPrize::getId);
        wrapper.last(" limit 1");
        return selectOne(wrapper);
    }

    /**
     * 活动已结束
     *
     * @param giftId
     * @param userId
     * @param turn
     * @return
     */
    default FishUserPrize getTimeGiveUp(Integer giftId, Integer userId, Integer turn) {
        LambdaQueryWrapper<FishUserPrize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FishUserPrize::getGiftId, giftId);
        wrapper.eq(FishUserPrize::getUserId, userId);
        wrapper.eq(FishUserPrize::getTurn, turn);
        wrapper.orderByDesc(FishUserPrize::getId);
        wrapper.last(" limit 1");
        return selectOne(wrapper);
    }

    default Page<FishUserPrize> findMyUserPrize(SearchFishMyUserPrizeCondition condition) {
        LambdaQueryWrapper<FishUserPrize> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FishUserPrize::getGiftId, condition.getGiftId());
        wrapper.eq(FishUserPrize::getUserId, condition.getUserId());
        wrapper.orderByDesc(FishUserPrize::getId);
        return selectPage(condition.getPage(), wrapper);
    }
}
