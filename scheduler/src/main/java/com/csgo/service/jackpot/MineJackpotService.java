package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.mine.MineJackpot;
import com.csgo.domain.plus.mine.MineJackpotBillRecord;
import com.csgo.domain.plus.mine.MineSpareJackpot;
import com.csgo.domain.plus.mine.MineSpareJackpotBillRecord;
import com.csgo.mapper.plus.mine.MineJackpotBillRecordMapper;
import com.csgo.mapper.plus.mine.MineJackpotMapper;
import com.csgo.mapper.plus.mine.MineSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.mine.MineSpareJackpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 扫雷玩法
 *
 * @author admin
 */
@Service
public class MineJackpotService {
    @Autowired
    private MineJackpotMapper mineJackpotMapper;
    @Autowired
    private MineSpareJackpotMapper mineSpareJackpotMapper;
    @Autowired
    private MineJackpotBillRecordMapper mineJackpotBillRecordMapper;
    @Autowired
    private MineSpareJackpotBillRecordMapper mineSpareJackpotBillRecordMapper;

    @Transactional
    public void record(List<MineJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        MineJackpot mineJackpot = mineJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        MineJackpotBillRecord mineJackpotBillRecord = records.get(0);
        mineJackpot.setBalance(mineJackpotBillRecord.getBalance());
        BaseEntity.updated(mineJackpot, source);
        mineJackpotMapper.updateById(mineJackpot);
        for (MineJackpotBillRecord record : records) {
            mineJackpotBillRecordMapper.insert(record);
        }
        if (mineJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = mineJackpotBillRecord.getAmount().divide(BigDecimal.valueOf(0.65), 2, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(0.35));
        MineSpareJackpot mineSpareJackpot = mineSpareJackpotMapper.selectOne(null);
        mineSpareJackpot.setBalance(mineSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(mineSpareJackpot, source);
        mineSpareJackpotMapper.updateById(mineSpareJackpot);
        MineSpareJackpotBillRecord record = new MineSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        mineSpareJackpotBillRecordMapper.insert(record);
    }

}
