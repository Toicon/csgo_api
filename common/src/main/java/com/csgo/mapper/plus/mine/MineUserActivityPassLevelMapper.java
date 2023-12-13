package com.csgo.mapper.plus.mine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.enums.PassStateEnum;
import com.csgo.domain.plus.mine.MineUserActivityPassLevel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 扫雷活动用户闯关信息
 *
 * @author admin
 */
@Repository
public interface MineUserActivityPassLevelMapper extends BaseMapper<MineUserActivityPassLevel> {
    /**
     * 返回活动奖励当前闯关层次
     *
     * @return
     */
    default MineUserActivityPassLevel getLastDrop(Integer activityId) {
        LambdaQueryWrapper<MineUserActivityPassLevel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MineUserActivityPassLevel::getActivityId, activityId);
        wrapper.orderByDesc(MineUserActivityPassLevel::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    /**
     * 返回20条最近通关记录
     *
     * @return
     */
    default List<MineUserActivityPassLevel> findLastDropList() {
        LambdaQueryWrapper<MineUserActivityPassLevel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MineUserActivityPassLevel::getLevel, 6);
        wrapper.eq(MineUserActivityPassLevel::getPassState, PassStateEnum.YES.getCode());
        wrapper.orderByDesc(MineUserActivityPassLevel::getId);
        wrapper.last("limit 20");
        return selectList(wrapper);
    }
}
