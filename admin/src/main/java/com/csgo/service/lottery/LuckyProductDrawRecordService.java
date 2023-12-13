package com.csgo.service.lottery;

import com.csgo.domain.plus.config.LuckyProductDrawRecord;
import com.csgo.mapper.plus.lottery.LuckyProductDrawRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author admin
 */
@Service
public class LuckyProductDrawRecordService {

    @Autowired
    private LuckyProductDrawRecordMapper luckyProductDrawRecordMapper;

    @Transactional
    public void batchInsert(List<LuckyProductDrawRecord> records) {
        for (LuckyProductDrawRecord record : records) {
            luckyProductDrawRecordMapper.insert(record);
        }
    }
}
