package com.csgo.service.lottery;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.mapper.plus.lottery.LotteryDrawRecordMapper;

/**
 * @author admin
 */
@Service
public class LotteryDrawRecordService {

    @Autowired
    private LotteryDrawRecordMapper lotteryDrawRecordMapper;

    @Transactional
    public void batchInsert(List<LotteryDrawRecord> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        for (LotteryDrawRecord record : records) {
            lotteryDrawRecordMapper.insert(record);
        }
    }
}
