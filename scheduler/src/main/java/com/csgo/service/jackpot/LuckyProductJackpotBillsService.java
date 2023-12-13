package com.csgo.service.jackpot;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.domain.plus.jackpot.LuckyProductJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.LuckyProductJackpotBillRecordMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class LuckyProductJackpotBillsService {

    @Autowired
    private LuckyProductJackpotBillRecordMapper jackpotBillRecordMapper;

    @Transactional
    public void record(List<LuckyProductJackpotBillRecord> records) {
        for (LuckyProductJackpotBillRecord record : records) {
            jackpotBillRecordMapper.insert(record);
        }
    }

}
