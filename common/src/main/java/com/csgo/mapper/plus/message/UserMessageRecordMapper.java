package com.csgo.mapper.plus.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.SearchUserMessageRecordCondition;
import com.csgo.domain.plus.message.UserMessageRecord;
import com.csgo.domain.report.StatisticsDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserMessageRecordMapper extends BaseMapper<UserMessageRecord> {

    default Page<UserMessageRecord> pagination(SearchUserMessageRecordCondition condition) {
        LambdaQueryWrapper<UserMessageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessageRecord::getUserId, condition.getUserId());
        wrapper.orderByDesc(UserMessageRecord::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    /**
     * 当日开箱次数
     *
     * @return
     */
    List<StatisticsDTO> countOpenCount(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("dataScope") String dataScope);

    /**
     * 当日开箱人数（活跃用户，即有开箱的人）
     *
     * @return
     */
    List<StatisticsDTO> countActiveCount(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("dataScope") String dataScope);

    int deleteBeforeTime(@Param("flag") int flag, @Param("beforeTime") Date beforeTime);
}