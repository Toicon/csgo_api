package com.csgo.service.fish;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.fish.FishAnchorJackpot;
import com.csgo.domain.plus.fish.FishAnchorJackpotBillRecord;
import com.csgo.domain.plus.fish.FishAnchorSpareJackpot;
import com.csgo.domain.plus.fish.FishAnchorSpareJackpotBillRecord;
import com.csgo.domain.plus.jackpot.JackpotOperationRecord;
import com.csgo.domain.plus.jackpot.JackpotType;
import com.csgo.mapper.plus.fish.FishAnchorJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishAnchorJackpotMapper;
import com.csgo.mapper.plus.fish.FishAnchorSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishAnchorSpareJackpotMapper;
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
 * 钓鱼玩法测试奖池
 *
 * @author admin
 */
@Service
public class FishAnchorJackpotService {
    private static final String FISH_ANCHOR_JACKPOT_BALANCE = "FISH_ANCHOR_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private FishAnchorJackpotMapper fishAnchorJackpotMapper;
    @Autowired
    private FishAnchorSpareJackpotMapper fishAnchorSpareJackpotMapper;
    @Autowired
    private FishAnchorJackpotBillRecordMapper fishAnchorJackpotBillRecordMapper;
    @Autowired
    private FishAnchorSpareJackpotBillRecordMapper fishAnchorSpareJackpotBillRecordMapper;
    @Autowired
    private JackpotOperationRecordMapper jackpotOperationRecordMapper;

    @Transactional
    public void record(List<FishAnchorJackpotBillRecord> records, String source) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        FishAnchorJackpot fishAnchorJackpot = fishAnchorJackpotMapper.selectOne(null);
        if (records.size() > 1) {
            return;
        }
        FishAnchorJackpotBillRecord fishAnchorJackpotBillRecord = records.get(0);
        fishAnchorJackpot.setBalance(fishAnchorJackpotBillRecord.getBalance());
        BaseEntity.updated(fishAnchorJackpot, source);
        fishAnchorJackpotMapper.updateById(fishAnchorJackpot);
        for (FishAnchorJackpotBillRecord record : records) {
            fishAnchorJackpotBillRecordMapper.insert(record);
        }
        if (fishAnchorJackpotBillRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal spare = fishAnchorJackpotBillRecord.getAmount().divide(new BigDecimal(9), 2, BigDecimal.ROUND_DOWN);
        FishAnchorSpareJackpot fishAnchorSpareJackpot = fishAnchorSpareJackpotMapper.selectOne(null);
        fishAnchorSpareJackpot.setBalance(fishAnchorSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(fishAnchorSpareJackpot, source);
        fishAnchorSpareJackpotMapper.updateById(fishAnchorSpareJackpot);
        FishAnchorSpareJackpotBillRecord record = new FishAnchorSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        fishAnchorSpareJackpotBillRecordMapper.insert(record);
    }

    public FishAnchorJackpot getFishAnchorJackpot() {
        return fishAnchorJackpotMapper.selectOne(null);
    }

    public FishAnchorSpareJackpot getFishAnchorSpareJackpot() {
        return fishAnchorSpareJackpotMapper.selectOne(null);
    }

    @Transactional
    public void updateFishAnchorJackpot(BigDecimal amount, String user) {
        FishAnchorJackpot fishAnchorJackpot = fishAnchorJackpotMapper.selectOne(null);
        //奖池操作记录
        JackpotOperationRecord record = new JackpotOperationRecord();
        record.setAmount(amount);
        record.setBeforeAmount(fishAnchorJackpot.getBalance());
        record.setJackpotType(JackpotType.MINE);
        BaseEntity.created(record, user);
        jackpotOperationRecordMapper.insert(record);
        fishAnchorJackpot.setBalance(amount);
        fishAnchorJackpotMapper.updateById(fishAnchorJackpot);
        redisTemplateFacde.set(getFishAnchorJackpotKey(), String.valueOf(amount));
    }

    public PageInfo<FishAnchorJackpotBillRecord> pageList(Integer pageNum, Integer pageSize) {
        Page<FishAnchorJackpotBillRecord> page = new Page<>(pageNum, pageSize);
        Page<FishAnchorJackpotBillRecord> billRecordPage = fishAnchorJackpotBillRecordMapper.page(page);
        return new PageInfo<>(billRecordPage);
    }


    private String getFishAnchorJackpotKey() {
        return Messages.format("balance:{}", FISH_ANCHOR_JACKPOT_BALANCE);
    }


}
