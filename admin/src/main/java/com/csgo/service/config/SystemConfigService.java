package com.csgo.service.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.config.SearchSystemConfigCondition;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author admin
 */
@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    public SystemConfig get(String key) {
        return systemConfigMapper.get(key);
    }

    public Page<SystemConfig> pagination(SearchSystemConfigCondition condition) {
        return systemConfigMapper.pagination(condition);
    }

    @Transactional
    public void update(SystemConfig config, String updateBy) {
        config.setUb(updateBy);
        config.setUt(new Date());
        systemConfigMapper.updateById(config);
    }

    @Transactional
    public void delete(int id) {
        systemConfigMapper.deleteById(id);
    }
}
