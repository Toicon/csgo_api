package com.csgo.modular.tendraw.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.modular.tendraw.domain.TenDrawGameDO;
import com.csgo.modular.tendraw.enums.TenDrawGameStatus;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawGameVM;
import com.csgo.modular.tendraw.model.admin.AdminTenDrawGameVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface TenDrawGameMapper extends BaseMapper<TenDrawGameDO> {

    Page<AdminTenDrawGameVO> getAdminPage(Page<TenDrawGameDO> page, @Param("condition") AdminTenDrawGameVM condition);

    default TenDrawGameDO getLastUnFinishGame(Integer userId) {
        LambdaQueryWrapper<TenDrawGameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenDrawGameDO::getUserId, userId);
        wrapper.eq(TenDrawGameDO::getState, TenDrawGameStatus.ING.getCode());

        wrapper.orderByDesc(TenDrawGameDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

}
