package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.BattleJackpot;
import com.csgo.domain.plus.jackpot.BattleJackpotBillRecord;
import com.csgo.domain.plus.jackpot.BattleSpareJackpot;
import com.csgo.domain.plus.jackpot.BattleSpareJackpotBillRecord;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.mapper.plus.jackpot.BattleJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BattleJackpotMapper;
import com.csgo.mapper.plus.jackpot.BattleSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.jackpot.BattleSpareJackpotMapper;
import com.csgo.mapper.plus.jackpot.JackpotOperationRecordMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.PageInfo;
import com.echo.framework.util.Messages;

/**
 * @author admin
 */
@Service
public class BattleJackpotService {
    private static final String BATTLE_JACKPOT_BALANCE = "BATTLE_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private BattleJackpotMapper battleJackpotMapper;
    @Autowired
    private BattleSpareJackpotMapper battleSpareJackpotMapper;
    @Autowired
    private BattleJackpotBillRecordMapper battleJackpotBillRecordMapper;
    @Autowired
    private BattleSpareJackpotBillRecordMapper battleSpareJackpotBillRecordMapper;
    @Autowired
    private JackpotOperationRecordMapper jackpotOperationRecordMapper;

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
        BigDecimal spare = battleJackpotBillRecord.getAmount().divide(new BigDecimal(9), 2, BigDecimal.ROUND_DOWN);
        BattleSpareJackpot battleSpareJackpot = battleSpareJackpotMapper.selectOne(null);
        battleSpareJackpot.setBalance(battleSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(battleSpareJackpot, source);
        battleSpareJackpotMapper.updateById(battleSpareJackpot);
        BattleSpareJackpotBillRecord record = new BattleSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        battleSpareJackpotBillRecordMapper.insert(record);
    }

    public BattleJackpot getBattleJackpot() {
        return battleJackpotMapper.selectOne(null);
    }

    public BattleSpareJackpot getBattleSpareJackpot() {
        return battleSpareJackpotMapper.selectOne(null);
    }

    @Transactional
    public void updateBattleJackpot(BigDecimal amount, String user) {
        BattleJackpot battleJackpot = battleJackpotMapper.selectOne(null);
        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(amount);
        record.setBeforeAmount(battleJackpot.getBalance());
        record.setJackpotType(JackpotType.BOX);
        BaseEntity.created(record, user);
        jackpotOperationRecordMapper.insert(record);
        battleJackpot.setBalance(amount);
        battleJackpotMapper.updateById(battleJackpot);
        redisTemplateFacde.set(getBattleJackpotKey(), String.valueOf(amount));
    }

    public PageInfo<BattleJackpotBillRecord> pageList(Integer pageNum, Integer pageSize) {
        Page<BattleJackpotBillRecord> page = new Page<>(pageNum, pageSize);
        Page<BattleJackpotBillRecord> billRecordPage = battleJackpotBillRecordMapper.page(page);
        return new PageInfo<>(billRecordPage);
    }


    private String getBattleJackpotKey() {
        return Messages.format("balance:{}", BATTLE_JACKPOT_BALANCE);
    }


}
