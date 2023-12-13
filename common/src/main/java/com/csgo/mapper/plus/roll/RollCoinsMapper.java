package com.csgo.mapper.plus.roll;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.SearchRollCoinsCondition;
import com.csgo.domain.plus.roll.RollCoins;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface RollCoinsMapper extends BaseMapper<RollCoins> {

    default Page<RollCoins> pagination(SearchRollCoinsCondition condition) {
        LambdaQueryWrapper<RollCoins> wrapper = Wrappers.lambdaQuery();
        if (null != condition.getRollId()) {
            wrapper.eq(RollCoins::getRollId, condition.getRollId());
        }
        wrapper.orderByDesc(RollCoins::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    default List<RollCoins> find(Integer rollId) {
        LambdaQueryWrapper<RollCoins> wrapper = Wrappers.lambdaQuery();
        if (null != rollId) {
            wrapper.eq(RollCoins::getRollId, rollId);
        }
        return selectList(wrapper);
    }

    default List<RollCoins> findByRollIds(Collection<Integer> rollIds) {
        LambdaQueryWrapper<RollCoins> wrapper = Wrappers.lambdaQuery();
        if (!CollectionUtils.isEmpty(rollIds)) {
            wrapper.in(RollCoins::getRollId, rollIds);
        }
        return selectList(wrapper);
    }
}
