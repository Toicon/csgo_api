package com.csgo.modular.bomb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.enums.NovBombGameStatus;
import com.csgo.modular.bomb.model.admin.AdminNovBombGameVM;
import com.csgo.modular.bomb.model.admin.AdminNovBombGameVO;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface NovBombGameMapper extends BaseMapper<NovBombGameDO> {

    default NovBombGameDO getLastUnFinishGame(Integer userId) {
        LambdaQueryWrapper<NovBombGameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGameDO::getUserId, userId);
        wrapper.eq(NovBombGameDO::getStatus, NovBombGameStatus.ING.getCode());

        wrapper.orderByDesc(NovBombGameDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default List<NovBombGameDO> getTodayLeaderboard(Integer num, Integer configId) {
        DateTime now = DateTime.now();
        DateTime todayStart = now.withTimeAtStartOfDay();

        LambdaQueryWrapper<NovBombGameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGameDO::getStatus, NovBombGameStatus.FINISHED.getCode());
        if (configId != null) {
            wrapper.eq(NovBombGameDO::getConfigId, configId);
        }
        wrapper.ge(NovBombGameDO::getRewardDate, todayStart.toDate());
        wrapper.orderByDesc(NovBombGameDO::getRewardProductPrice);
        wrapper.orderByAsc(NovBombGameDO::getId);
        wrapper.last(String.format("limit %s", num));
        return selectList(wrapper);
    }

    default List<NovBombGameDO> getRecent(Integer num, Integer configId) {
        LambdaQueryWrapper<NovBombGameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGameDO::getStatus, NovBombGameStatus.FINISHED.getCode());
        if (configId != null) {
            wrapper.eq(NovBombGameDO::getConfigId, configId);
        }
        wrapper.orderByDesc(NovBombGameDO::getId);
        wrapper.last(String.format("limit %s", num));
        return selectList(wrapper);
    }

    Page<AdminNovBombGameVO> getAdminPage(Page<NovBombGameDO> mpPage, @Param("condition") AdminNovBombGameVM condition);

}
