package com.csgo.service.log;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.log.SearchLotteryDrawRecordCondition;
import com.csgo.condition.log.SearchLuckyProductDrawRecordCondition;
import com.csgo.domain.plus.config.LuckyProductDrawRecord;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.mapper.plus.lottery.LotteryDrawRecordMapper;
import com.csgo.mapper.plus.lottery.LuckyProductDrawRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class LogService {

    @Autowired
    private LotteryDrawRecordMapper lotteryDrawRecordMapper;
    @Autowired
    private LuckyProductDrawRecordMapper luckyProductDrawRecordMapper;

    public Page<LotteryDrawRecord> pagination(SearchLotteryDrawRecordCondition condition) {
        return lotteryDrawRecordMapper.pagination(condition);
    }

    public Page<LuckyProductDrawRecord> pagination(SearchLuckyProductDrawRecordCondition condition) {
        return luckyProductDrawRecordMapper.pagination(condition);
    }
}
