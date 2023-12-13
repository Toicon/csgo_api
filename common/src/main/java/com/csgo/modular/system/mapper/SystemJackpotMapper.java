package com.csgo.modular.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.modular.system.domain.SystemJackpotDO;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface SystemJackpotMapper extends BaseMapper<SystemJackpotDO> {

    default SystemJackpotDO selectByType(Integer type) {
        LambdaQueryWrapper<SystemJackpotDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemJackpotDO::getType, type);
        wrapper.orderByAsc(SystemJackpotDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

}
