package com.csgo.mapper.plus.membership;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.membership.SearchMembershipLevelConfigCondition;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MembershipLevelConfigMapper extends BaseMapper<MembershipLevelConfig> {

    /**
     * 分页查询表membership_level所有信息
     */
    default Page<MembershipLevelConfig> pagination(SearchMembershipLevelConfigCondition condition) {
        LambdaQueryWrapper<MembershipLevelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MembershipLevelConfig::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    default List<MembershipLevelConfig> findLevelLimit(Integer level, BigDecimal levelLimit) {
        LambdaQueryWrapper<MembershipLevelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(MembershipLevelConfig::getLevel, level).le(MembershipLevelConfig::getLevelLimit, levelLimit);
        wrapper.or();
        wrapper.lt(MembershipLevelConfig::getLevel, level).ge(MembershipLevelConfig::getLevelLimit, levelLimit);
        return selectList(wrapper);
    }

    default MembershipLevelConfig findByLevel(Integer level) {
        LambdaQueryWrapper<MembershipLevelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MembershipLevelConfig::getLevel, level);
        return selectOne(wrapper);
    }

    default List<MembershipLevelConfig> maxLevel(BigDecimal growth) {
        LambdaQueryWrapper<MembershipLevelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(MembershipLevelConfig::getLevelLimit, growth);
        wrapper.orderByDesc(MembershipLevelConfig::getLevel);
        return selectList(wrapper);
    }


    default List<MembershipLevelConfig> list() {
        LambdaQueryWrapper<MembershipLevelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MembershipLevelConfig::getLevel);
        return selectList(wrapper);
    }


    default MembershipLevelConfig nextLevel(Integer level) {
        LambdaQueryWrapper<MembershipLevelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(MembershipLevelConfig::getLevel, level);
        wrapper.orderByAsc(MembershipLevelConfig::getLevel);
        wrapper.last(" limit 1");
        return selectOne(wrapper);
    }

    default List<MembershipLevelConfig> findLeLevel(Integer level) {
        LambdaQueryWrapper<MembershipLevelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(MembershipLevelConfig::getLevel, level);
        return selectList(wrapper);
    }
}

