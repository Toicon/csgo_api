package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.BoxJackpot;
import com.csgo.domain.plus.jackpot.BoxJackpotBillRecord;
import com.csgo.domain.plus.jackpot.BoxSpareJackpot;
import com.csgo.domain.plus.jackpot.BoxSpareJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.BoxJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BoxJackpotMapper;
import com.csgo.mapper.plus.jackpot.BoxSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BoxSpareJackpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Service
public class BoxJackpotService {
    @Autowired
    private BoxJackpotMapper boxJackpotMapper;
    @Autowired
    private BoxSpareJackpotMapper boxSpareJackpotMapper;
    @Autowired
    private BoxJackpotBillRecordMapper boxJackpotBillRecordMapper;
    @Autowired
    private BoxSpareJackpotBillRecordMapper boxSpareJackpotBillRecordMapper;

    @Transactional
    public void record(List<BoxJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        BoxJackpot boxJackpot = boxJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        BoxJackpotBillRecord boxJackpotBillRecord = records.get(0);
        boxJackpot.setBalance(boxJackpotBillRecord.getBalance());
        BaseEntity.updated(boxJackpot, source);
        boxJackpotMapper.updateById(boxJackpot);
        for (BoxJackpotBillRecord record : records) {
            boxJackpotBillRecordMapper.insert(record);
        }
        if (boxJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = boxJackpotBillRecord.getAmount().divide(BigDecimal.valueOf(0.65), 2, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(0.35));
        BoxSpareJackpot boxSpareJackpot = boxSpareJackpotMapper.selectOne(null);
        boxSpareJackpot.setBalance(boxSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(boxSpareJackpot, source);
        boxSpareJackpotMapper.updateById(boxSpareJackpot);
        BoxSpareJackpotBillRecord record = new BoxSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        boxSpareJackpotBillRecordMapper.insert(record);
    }

    public void recordKey(List<BoxJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records) || records.size() > 1) {
            return;
        }
        BoxJackpotBillRecord boxJackpotBillRecord = records.get(0);

        BoxJackpot boxJackpot = boxJackpotMapper.selectOne(null);
        boxJackpot.setBalance(boxJackpotBillRecord.getBalance());
        BaseEntity.updated(boxJackpot, source);
        boxJackpotMapper.updateById(boxJackpot);
        for (BoxJackpotBillRecord record : records) {
            boxJackpotBillRecordMapper.insert(record);
        }
    }

}
