package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.domain.plus.jackpot.UpgradeJackpot;
import com.csgo.domain.plus.jackpot.UpgradeJackpotBillRecord;
import com.csgo.domain.plus.jackpot.UpgradeSpareJackpot;
import com.csgo.domain.plus.jackpot.UpgradeSpareJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.JackpotOperationRecordMapper;
import com.csgo.mapper.plus.jackpot.UpgradeJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.UpgradeJackpotMapper;
import com.csgo.mapper.plus.jackpot.UpgradeSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.UpgradeSpareJackpotMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.PageInfo;
import com.echo.framework.util.Messages;

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
    @Autowired
    private JackpotOperationRecordMapper jackpotOperationRecordMapper;

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
        BigDecimal spare = upgradeJackpotBillRecord.getAmount().divide(new BigDecimal(19), 2, BigDecimal.ROUND_DOWN);
        UpgradeSpareJackpot upgradeSpareJackpot = upgradeSpareJackpotMapper.selectOne(null);
        upgradeSpareJackpot.setBalance(upgradeSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(upgradeSpareJackpot, source);
        upgradeSpareJackpotMapper.updateById(upgradeSpareJackpot);
        UpgradeSpareJackpotBillRecord record = new UpgradeSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        upgradeSpareJackpotBillRecordMapper.insert(record);
    }

    public UpgradeJackpot getUpgradeJackpot() {
        return upgradeJackpotMapper.selectOne(null);
    }

    public UpgradeSpareJackpot getUpgradeSpareJackpot() {
        return upgradeSpareJackpotMapper.selectOne(null);
    }

    @Transactional
    public void updateUpgradeJackpot(BigDecimal amount, String user) {
        UpgradeJackpot upgradeJackpot = upgradeJackpotMapper.selectOne(null);
        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(amount);
        record.setBeforeAmount(upgradeJackpot.getBalance());
        record.setJackpotType(JackpotType.BOX);
        BaseEntity.created(record, user);
        jackpotOperationRecordMapper.insert(record);
        upgradeJackpot.setBalance(amount);
        upgradeJackpotMapper.updateById(upgradeJackpot);
        redisTemplateFacde.set(getUpgradeJackpotKey(), String.valueOf(amount));
    }

    public PageInfo<UpgradeJackpotBillRecord> pageList(Integer pageNum, Integer pageSize) {
        Page<UpgradeJackpotBillRecord> page = new Page<>(pageNum, pageSize);
        Page<UpgradeJackpotBillRecord> billRecordPage = upgradeJackpotBillRecordMapper.page(page);
        return new PageInfo<>(billRecordPage);
    }


    private String getUpgradeJackpotKey() {
        return Messages.format("balance:{}", UPGRADE_JACKPOT_BALANCE);
    }


}
