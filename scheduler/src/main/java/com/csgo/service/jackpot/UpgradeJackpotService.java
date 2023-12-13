package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.UpgradeJackpot;
import com.csgo.domain.plus.jackpot.UpgradeJackpotBillRecord;
import com.csgo.domain.plus.jackpot.UpgradeSpareJackpot;
import com.csgo.domain.plus.jackpot.UpgradeSpareJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.UpgradeJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.UpgradeJackpotMapper;
import com.csgo.mapper.plus.jackpot.UpgradeSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.UpgradeSpareJackpotMapper;
import com.csgo.redis.RedisTemplateFacde;
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
public class UpgradeJackpotService {
    private static final String UPGRADE_JACKPOT_BALANCE = "UPGRADE_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private UpgradeJackpotMapper upgradeJackpotMapper;
    @Autowired
    private UpgradeSpareJackpotMapper upgradeSpareJackpotMapper;
    @Autowired
    private UpgradeJackpotBillRecordMapper upgradeJackpotBillRecordMapper;
    @Autowired
    private UpgradeSpareJackpotBillRecordMapper upgradeSpareJackpotBillRecordMapper;

    @Transactional
    public void record(List<UpgradeJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        UpgradeJackpot upgradeJackpot = upgradeJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        UpgradeJackpotBillRecord upgradeJackpotBillRecord = records.get(0);
        upgradeJackpot.setBalance(upgradeJackpotBillRecord.getBalance());
        BaseEntity.updated(upgradeJackpot, source);
        upgradeJackpotMapper.updateById(upgradeJackpot);
        for (UpgradeJackpotBillRecord record : records) {
            upgradeJackpotBillRecordMapper.insert(record);
        }
        if (upgradeJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = upgradeJackpotBillRecord.getAmount().divide(BigDecimal.valueOf(0.65), 2, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(0.35));
        UpgradeSpareJackpot upgradeSpareJackpot = upgradeSpareJackpotMapper.selectOne(null);
        upgradeSpareJackpot.setBalance(upgradeSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(upgradeSpareJackpot, source);
        upgradeSpareJackpotMapper.updateById(upgradeSpareJackpot);
        UpgradeSpareJackpotBillRecord record = new UpgradeSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        upgradeSpareJackpotBillRecordMapper.insert(record);
    }

}
