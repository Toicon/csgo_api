package com.csgo.mapper.plus.lottery;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.log.SearchLotteryDrawRecordCondition;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface LotteryDrawRecordMapper extends BaseMapper<LotteryDrawRecord> {

    default Page<LotteryDrawRecord> pagination(SearchLotteryDrawRecordCondition condition) {
        LambdaQueryWrapper<LotteryDrawRecord> wrapper = new LambdaQueryWrapper<>();
        if (condition.getUserId() != null) {
            wrapper.eq(LotteryDrawRecord::getUserId, condition.getUserId());
        }
        if (null != condition.getType()) {
            wrapper.eq(LotteryDrawRecord::getType, condition.getType());
        }
        if (null != condition.getStartDate()) {
            wrapper.ge(LotteryDrawRecord::getCt, condition.getStartDate());
        }
        if (null != condition.getEndDate()) {
            wrapper.le(LotteryDrawRecord::getCt, condition.getEndDate());
        }
        wrapper.orderByDesc(LotteryDrawRecord::getCt);
        return selectPage(condition.getPage(), wrapper);
    }
}
