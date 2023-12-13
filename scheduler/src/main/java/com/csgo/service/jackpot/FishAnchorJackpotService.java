package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.fish.FishAnchorJackpot;
import com.csgo.domain.plus.fish.FishAnchorJackpotBillRecord;
import com.csgo.domain.plus.fish.FishAnchorSpareJackpot;
import com.csgo.domain.plus.fish.FishAnchorSpareJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.fish.FishAnchorJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishAnchorJackpotMapper;
import com.csgo.mapper.plus.fish.FishAnchorSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishAnchorSpareJackpotMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.util.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 钓鱼测试
 */
@Service
public class FishAnchorJackpotService {
    private static final String FISH_ANCHOR_JACKPOT_BALANCE = "FISH_ANCHOR_JACKPOT_BALANCE";
    @Autowired
    private FishAnchorJackpotMapper fishAnchorJackpotMapper;
    @Autowired
    private FishAnchorSpareJackpotMapper fishAnchorSpareJackpotMapper;
    @Autowired
    private FishAnchorJackpotBillRecordMapper fishAnchorJackpotBillRecordMapper;
    @Autowired
    private FishAnchorSpareJackpotBillRecordMapper fishAnchorSpareJackpotBillRecordMapper;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

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
        BigDecimal spare = fishAnchorJackpotBillRecord.getAmount().divide(new BigDecimal(0.9), 2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(0.1));
        FishAnchorSpareJackpot fishAnchorSpareJackpot = fishAnchorSpareJackpotMapper.selectOne(null);
        fishAnchorSpareJackpot.setBalance(fishAnchorSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(fishAnchorSpareJackpot, source);
        fishAnchorSpareJackpotMapper.updateById(fishAnchorSpareJackpot);
        FishAnchorSpareJackpotBillRecord record = new FishAnchorSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        fishAnchorSpareJackpotBillRecordMapper.insert(record);
    }

    public BigDecimal getFishAnchorJackpotBalance() {
        FishAnchorJackpot jackpot = fishAnchorJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    public BigDecimal getFishAnchorJackpot() {
        String balance = redisTemplateFacde.get(getFishAnchorJackpotKey());
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = getFishAnchorJackpotBalance();
        redisTemplateFacde.set(getFishAnchorJackpotKey(), String.valueOf(existBalance));
        return existBalance;
    }

    public boolean hit(BigDecimal productPrice, boolean isSure) {
        BigDecimal boxJackpot = getFishAnchorJackpot();
        if (boxJackpot.compareTo(productPrice) < 0 && !isSure) {
            return false;
        }
        return true;
    }

    public void updateFishAnchorJackpot(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getFishAnchorJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getFishAnchorJackpotKey(), String.valueOf(newBalance));
        FishAnchorJackpotBillRecord record = new FishAnchorJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());
        List<FishAnchorJackpotBillRecord> records = new ArrayList<>();
        records.add(record);
        this.record(records, "FishAnchorJackpotListener");
    }

    private String getFishAnchorJackpotKey() {
        return Messages.format("balance:{}", FISH_ANCHOR_JACKPOT_BALANCE);
    }
}
