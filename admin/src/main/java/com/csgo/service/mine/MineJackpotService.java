package com.csgo.service.mine;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.domain.plus.mine.MineJackpot;
import com.csgo.domain.plus.mine.MineJackpotBillRecord;
import com.csgo.domain.plus.mine.MineSpareJackpot;
import com.csgo.domain.plus.mine.MineSpareJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.JackpotOperationRecordMapper;
import com.csgo.mapper.plus.mine.MineJackpotBillRecordMapper;
import com.csgo.mapper.plus.mine.MineJackpotMapper;
import com.csgo.mapper.plus.mine.MineSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.mine.MineSpareJackpotMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.PageInfo;
import com.echo.framework.util.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 扫雷活动
 *
 * @author admin
 */
@Service
public class MineJackpotService {
    private static final String MINE_JACKPOT_BALANCE = "MINE_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private MineJackpotMapper mineJackpotMapper;
    @Autowired
    private MineSpareJackpotMapper mineSpareJackpotMapper;
    @Autowired
    private MineJackpotBillRecordMapper mineJackpotBillRecordMapper;
    @Autowired
    private MineSpareJackpotBillRecordMapper mineSpareJackpotBillRecordMapper;
    @Autowired
    private JackpotOperationRecordMapper jackpotOperationRecordMapper;

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
        BigDecimal spare = mineJackpotBillRecord.getAmount().divide(new BigDecimal(9), 2, BigDecimal.ROUND_DOWN);
        MineSpareJackpot mineSpareJackpot = mineSpareJackpotMapper.selectOne(null);
        mineSpareJackpot.setBalance(mineSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(mineSpareJackpot, source);
        mineSpareJackpotMapper.updateById(mineSpareJackpot);
        MineSpareJackpotBillRecord record = new MineSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        mineSpareJackpotBillRecordMapper.insert(record);
    }

    public MineJackpot getMineJackpot() {
        return mineJackpotMapper.selectOne(null);
    }

    public MineSpareJackpot getMineSpareJackpot() {
        return mineSpareJackpotMapper.selectOne(null);
    }

    @Transactional
    public void updateMineJackpot(BigDecimal amount, String user) {
        MineJackpot mineJackpot = mineJackpotMapper.selectOne(null);
        //奖池操作记录
        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(amount);
        record.setBeforeAmount(mineJackpot.getBalance());
        record.setJackpotType(JackpotType.MINE);
        BaseEntity.created(record, user);
        jackpotOperationRecordMapper.insert(record);
        mineJackpot.setBalance(amount);
        mineJackpotMapper.updateById(mineJackpot);
        redisTemplateFacde.set(getMineJackpotKey(), String.valueOf(amount));
    }

    public PageInfo<MineJackpotBillRecord> pageList(Integer pageNum, Integer pageSize) {
        Page<MineJackpotBillRecord> page = new Page<>(pageNum, pageSize);
        Page<MineJackpotBillRecord> billRecordPage = mineJackpotBillRecordMapper.page(page);
        return new PageInfo<>(billRecordPage);
    }


    private String getMineJackpotKey() {
        return Messages.format("balance:{}", MINE_JACKPOT_BALANCE);
    }


}
