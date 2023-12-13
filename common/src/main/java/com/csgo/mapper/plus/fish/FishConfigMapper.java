package com.csgo.mapper.plus.fish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.fish.FishConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 钓鱼活动配置信息
 */
@Repository
public interface FishConfigMapper extends BaseMapper<FishConfig> {
    /**
     * 根据场次类型返回鱼竿信息
     *
     * @param sessionType
     * @return
     */
    default List<FishConfig> findBySessionType(Integer sessionType) {
        LambdaQueryWrapper<FishConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FishConfig::getSessionType, sessionType);
        return selectList(wrapper);
    }


}
