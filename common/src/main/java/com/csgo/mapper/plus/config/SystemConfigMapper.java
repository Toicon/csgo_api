package com.csgo.mapper.plus.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.config.SearchSystemConfigCondition;
import com.csgo.domain.plus.config.SystemConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    default SystemConfig get(String key) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getKey, key);
        return selectOne(wrapper);
    }

    default Page<SystemConfig> pagination(SearchSystemConfigCondition condition) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SystemConfig::getKey, condition.getPrefix() + "%");
        return selectPage(condition.getPage(), wrapper);
    }

    default List<SystemConfig> findByPrefix(String prefix) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SystemConfig::getKey, prefix + "%");
        return selectList(wrapper);
    }
}
