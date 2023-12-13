package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.anchor.BoxAnchorJackpot;
import com.csgo.domain.plus.anchor.BoxAnchorJackpotBillRecord;
import com.csgo.domain.plus.anchor.BoxAnchorSpareJackpot;
import com.csgo.domain.plus.anchor.BoxAnchorSpareJackpotBillRecord;
import com.csgo.mapper.plus.anchor.BoxAnchorJackpotBillRecordMapper;
import com.csgo.mapper.plus.anchor.BoxAnchorJackpotMapper;
import com.csgo.mapper.plus.anchor.BoxAnchorSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.anchor.BoxAnchorSpareJackpotMapper;
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
public class BoxAnchorJackpotService {
    @Autowired
    private BoxAnchorJackpotMapper boxAnchorJackpotMapper;
    @Autowired
    private BoxAnchorSpareJackpotMapper boxAnchorSpareJackpotMapper;
    @Autowired
    private BoxAnchorJackpotBillRecordMapper boxAnchorJackpotBillRecordMapper;
    @Autowired
    private BoxAnchorSpareJackpotBillRecordMapper boxAnchorSpareJackpotBillRecordMapper;

    @Transactional
    public void record(List<BoxAnchorJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        BoxAnchorJackpot boxAnchorJackpot = boxAnchorJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        BoxAnchorJackpotBillRecord boxAnchorJackpotBillRecord = records.get(0);
        boxAnchorJackpot.setBalance(boxAnchorJackpotBillRecord.getBalance());
        BaseEntity.updated(boxAnchorJackpot, source);
        boxAnchorJackpotMapper.updateById(boxAnchorJackpot);
        for (BoxAnchorJackpotBillRecord record : records) {
            boxAnchorJackpotBillRecordMapper.insert(record);
        }
        if (boxAnchorJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = boxAnchorJackpotBillRecord.getAmount().divide(BigDecimal.valueOf(0.8), 2, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(0.2));
        BoxAnchorSpareJackpot boxAnchorSpareJackpot = boxAnchorSpareJackpotMapper.selectOne(null);
        boxAnchorSpareJackpot.setBalance(boxAnchorSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(boxAnchorSpareJackpot, source);
        boxAnchorSpareJackpotMapper.updateById(boxAnchorSpareJackpot);
        BoxAnchorSpareJackpotBillRecord record = new BoxAnchorSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        boxAnchorSpareJackpotBillRecordMapper.insert(record);
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordKey(List<BoxAnchorJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records) || records.size() > 1) {
            return;
        }
        BoxAnchorJackpotBillRecord boxAnchorJackpotBillRecord = records.get(0);

        BoxAnchorJackpot boxAnchorJackpot = boxAnchorJackpotMapper.selectOne(null);
        boxAnchorJackpot.setBalance(boxAnchorJackpotBillRecord.getBalance());
        BaseEntity.updated(boxAnchorJackpot, source);
        boxAnchorJackpotMapper.updateById(boxAnchorJackpot);
        for (BoxAnchorJackpotBillRecord record : records) {
            boxAnchorJackpotBillRecordMapper.insert(record);
        }
    }

}
