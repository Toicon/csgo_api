package com.csgo.mapper.plus.fish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.fish.SearchFishBaitConfigCondition;
import com.csgo.domain.plus.fish.FishBaitConfig;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 钓鱼活动鱼饵配置信息
 */
@Repository
public interface FishBaitConfigMapper extends BaseMapper<FishBaitConfig> {
    default Page<FishBaitConfig> pagination(SearchFishBaitConfigCondition condition) {
        LambdaQueryWrapper<FishBaitConfig> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getBaitName())) {
            wrapper.like(FishBaitConfig::getBaitName, condition.getBaitName());
        }
        if (condition.getSessionType() != null) {
            wrapper.eq(FishBaitConfig::getSessionType, condition.getSessionType());
        }
        wrapper.orderByAsc(FishBaitConfig::getSessionType);
        return selectPage(condition.getPage(), wrapper);
    }

    /**
     * 根据场次类型返回鱼竿信息
     *
     * @param sessionType
     * @return
     */
    default List<FishBaitConfig> findBySessionType(Integer sessionType) {
        LambdaQueryWrapper<FishBaitConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FishBaitConfig::getSessionType, sessionType);
        wrapper.orderByAsc(FishBaitConfig::getPayRatio);
        return selectList(wrapper);
    }

}
