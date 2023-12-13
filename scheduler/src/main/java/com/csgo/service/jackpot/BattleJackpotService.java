package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.BattleJackpot;
import com.csgo.domain.plus.jackpot.BattleJackpotBillRecord;
import com.csgo.domain.plus.jackpot.BattleSpareJackpot;
import com.csgo.domain.plus.jackpot.BattleSpareJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.BattleJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BattleJackpotMapper;
import com.csgo.mapper.plus.jackpot.BattleSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BattleSpareJackpotMapper;
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
public class BattleJackpotService {
    @Autowired
    private BattleJackpotMapper battleJackpotMapper;
    @Autowired
    private BattleSpareJackpotMapper battleSpareJackpotMapper;
    @Autowired
    private BattleJackpotBillRecordMapper battleJackpotBillRecordMapper;
    @Autowired
    private BattleSpareJackpotBillRecordMapper battleSpareJackpotBillRecordMapper;

    @Transactional
    public void record(List<BattleJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        BattleJackpot battleJackpot = battleJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        BattleJackpotBillRecord battleJackpotBillRecord = records.get(0);
        battleJackpot.setBalance(battleJackpotBillRecord.getBalance());
        BaseEntity.updated(battleJackpot, source);
        battleJackpotMapper.updateById(battleJackpot);
        for (BattleJackpotBillRecord record : records) {
            battleJackpotBillRecordMapper.insert(record);
        }
        if (battleJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = battleJackpotBillRecord.getAmount().divide(new BigDecimal(8.5), 2, BigDecimal.ROUND_DOWN);
        BattleSpareJackpot battleSpareJackpot = battleSpareJackpotMapper.selectOne(null);
        battleSpareJackpot.setBalance(battleSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(battleSpareJackpot, source);
        battleSpareJackpotMapper.updateById(battleSpareJackpot);
        BattleSpareJackpotBillRecord record = new BattleSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        battleSpareJackpotBillRecordMapper.insert(record);
    }

}
