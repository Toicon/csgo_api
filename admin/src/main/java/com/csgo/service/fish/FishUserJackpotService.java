package com.csgo.service.fish;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.fish.FishUserJackpot;
import com.csgo.domain.plus.fish.FishUserJackpotBillRecord;
import com.csgo.domain.plus.fish.FishUserSpareJackpot;
import com.csgo.domain.plus.fish.FishUserSpareJackpotBillRecord;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.mapper.plus.fish.FishUserJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishUserJackpotMapper;
import com.csgo.mapper.plus.fish.FishUserSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishUserSpareJackpotMapper;
import com.csgo.mapper.plus.jackpot.JackpotOperationRecordMapper;
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
 * 钓鱼玩法散户奖池
 *
 * @author admin
 */
@Service
public class FishUserJackpotService {
    private static final String FISH_USER_JACKPOT_BALANCE = "FISH_USER_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private FishUserJackpotMapper fishUserJackpotMapper;
    @Autowired
    private FishUserSpareJackpotMapper fishUserSpareJackpotMapper;
    @Autowired
    private FishUserJackpotBillRecordMapper fishUserJackpotBillRecordMapper;
    @Autowired
    private FishUserSpareJackpotBillRecordMapper fishUserSpareJackpotBillRecordMapper;
    @Autowired
    private JackpotOperationRecordMapper jackpotOperationRecordMapper;

    @Transactional
    public void record(List<FishUserJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        FishUserJackpot fishUserJackpot = fishUserJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        FishUserJackpotBillRecord fishUserJackpotBillRecord = records.get(0);
        fishUserJackpot.setBalance(fishUserJackpotBillRecord.getBalance());
        BaseEntity.updated(fishUserJackpot, source);
        fishUserJackpotMapper.updateById(fishUserJackpot);
        for (FishUserJackpotBillRecord record : records) {
            fishUserJackpotBillRecordMapper.insert(record);
        }
        if (fishUserJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = fishUserJackpotBillRecord.getAmount().divide(new BigDecimal(9), 2, BigDecimal.ROUND_DOWN);
        FishUserSpareJackpot fishUserSpareJackpot = fishUserSpareJackpotMapper.selectOne(null);
        fishUserSpareJackpot.setBalance(fishUserSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(fishUserSpareJackpot, source);
        fishUserSpareJackpotMapper.updateById(fishUserSpareJackpot);
        FishUserSpareJackpotBillRecord record = new FishUserSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        fishUserSpareJackpotBillRecordMapper.insert(record);
    }

    public FishUserJackpot getFishUserJackpot() {
        return fishUserJackpotMapper.selectOne(null);
    }

    public FishUserSpareJackpot getFishUserSpareJackpot() {
        return fishUserSpareJackpotMapper.selectOne(null);
    }

    @Transactional
    public void updateFishUserJackpot(BigDecimal amount, String user) {
        FishUserJackpot fishUserJackpot = fishUserJackpotMapper.selectOne(null);
        //奖池操作记录
        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(amount);
        record.setBeforeAmount(fishUserJackpot.getBalance());
        record.setJackpotType(JackpotType.MINE);
        BaseEntity.created(record, user);
        jackpotOperationRecordMapper.insert(record);
        fishUserJackpot.setBalance(amount);
        fishUserJackpotMapper.updateById(fishUserJackpot);
        redisTemplateFacde.set(getFishUserJackpotKey(), String.valueOf(amount));
    }

    public PageInfo<FishUserJackpotBillRecord> pageList(Integer pageNum, Integer pageSize) {
        Page<FishUserJackpotBillRecord> page = new Page<>(pageNum, pageSize);
        Page<FishUserJackpotBillRecord> billRecordPage = fishUserJackpotBillRecordMapper.page(page);
        return new PageInfo<>(billRecordPage);
    }


    private String getFishUserJackpotKey() {
        return Messages.format("balance:{}", FISH_USER_JACKPOT_BALANCE);
    }


}
