package com.csgo.modular.bomb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.modular.bomb.domain.NovBombProductDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface NovBombGameProductMapper extends BaseMapper<NovBombProductDO> {

    default List<NovBombProductDO> listByPlayId(Integer playId) {
        LambdaQueryWrapper<NovBombProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombProductDO::getPlayId, playId);
        return selectList(wrapper);
    }

}
