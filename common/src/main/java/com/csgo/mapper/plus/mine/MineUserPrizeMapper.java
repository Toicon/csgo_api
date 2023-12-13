package com.csgo.mapper.plus.mine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.mine.SearchMinePriceCondition;
import com.csgo.domain.plus.mine.MineUserPrize;
import com.csgo.modular.product.model.front.ProductSimpleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 扫雷活动--用户奖励查询
 *
 * @author admin
 */
@Repository
public interface MineUserPrizeMapper extends BaseMapper<MineUserPrize> {
    default Page<MineUserPrize> pagination(SearchMinePriceCondition condition) {
        LambdaQueryWrapper<MineUserPrize> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getUserName())) {
            wrapper.like(MineUserPrize::getUserName, condition.getUserName());
        }
        if (condition.getPrizePriceMin() != null) {
            wrapper.ge(MineUserPrize::getPrizePrice, condition.getPrizePriceMin());
        }
        if (condition.getPrizePriceMax() != null) {
            wrapper.le(MineUserPrize::getPrizePrice, condition.getPrizePriceMax());
        }
        wrapper.orderByDesc(MineUserPrize::getCreateDate);
        return selectPage(condition.getPage(), wrapper);
    }

    ProductSimpleVO getMaxPriceProduct(@Param("userId") Integer userId, @Param("startDate") Date startDate);

    /**
     * 返回掉落奖励
     *
     * @param activityIds
     * @return
     */
    default List<MineUserPrize> findLastDropList(Collection<Integer> activityIds) {
        LambdaQueryWrapper<MineUserPrize> wrapper = Wrappers.lambdaQuery();
        wrapper.in(MineUserPrize::getActivityId, activityIds);
        wrapper.orderByDesc(MineUserPrize::getId);
        return selectList(wrapper);
    }
}
