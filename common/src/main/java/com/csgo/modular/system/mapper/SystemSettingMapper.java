package com.csgo.modular.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.modular.system.domain.SystemSettingDO;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface SystemSettingMapper extends BaseMapper<SystemSettingDO> {

    default SystemSettingDO getByKey(String key) {
        LambdaQueryWrapper<SystemSettingDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSettingDO::getConfigKey, key);
        wrapper.orderByAsc(SystemSettingDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

}
