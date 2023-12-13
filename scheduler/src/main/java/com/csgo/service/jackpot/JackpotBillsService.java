package com.csgo.service.jackpot;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.csgo.domain.plus.jackpot.JackpotBillRecord;
import com.csgo.mapper.plus.jackpot.JackpotBillRecordMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class JackpotBillsService {

    @Autowired
    private JackpotBillRecordMapper jackpotBillRecordMapper;

    @Transactional
    public void record(List<JackpotBillRecord> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        for (JackpotBillRecord record : records) {
            jackpotBillRecordMapper.insert(record);
        }
    }

}
