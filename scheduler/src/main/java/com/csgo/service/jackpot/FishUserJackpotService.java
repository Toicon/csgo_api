package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.fish.FishUserJackpot;
import com.csgo.domain.plus.fish.FishUserJackpotBillRecord;
import com.csgo.domain.plus.fish.FishUserSpareJackpot;
import com.csgo.domain.plus.fish.FishUserSpareJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.fish.FishUserJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishUserJackpotMapper;
import com.csgo.mapper.plus.fish.FishUserSpareJackpotBillRecordMapper;
import com.csgo.mapper.plus.fish.FishUserSpareJackpotMapper;
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
 * 钓鱼散户
 */
@Service
public class FishUserJackpotService {
    private static final String FISH_USER_JACKPOT_BALANCE = "FISH_USER_JACKPOT_BALANCE";
    @Autowired
    private FishUserJackpotMapper fishUserJackpotMapper;
    @Autowired
    private FishUserSpareJackpotMapper fishUserSpareJackpotMapper;
    @Autowired
    private FishUserJackpotBillRecordMapper fishUserJackpotBillRecordMapper;
    @Autowired
    private FishUserSpareJackpotBillRecordMapper fishUserSpareJackpotBillRecordMapper;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    public BigDecimal getFishUserJackpotBalance() {
        FishUserJackpot jackpot = fishUserJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

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
        BigDecimal spare = fishUserJackpotBillRecord.getAmount().divide(new BigDecimal(0.8), 2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(0.2));
        FishUserSpareJackpot fishUserSpareJackpot = fishUserSpareJackpotMapper.selectOne(null);
        fishUserSpareJackpot.setBalance(fishUserSpareJackpot.getBalance().add(spare));
        BaseEntity.updated(fishUserSpareJackpot, source);
        fishUserSpareJackpotMapper.updateById(fishUserSpareJackpot);
        FishUserSpareJackpotBillRecord record = new FishUserSpareJackpotBillRecord();
        record.setAmount(spare);
        BaseEntity.created(record, source);
        fishUserSpareJackpotBillRecordMapper.insert(record);
    }

    public BigDecimal getFishUserJackpot() {
        String balance = redisTemplateFacde.get(getFishUserJackpotKey());
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = getFishUserJackpotBalance();
        redisTemplateFacde.set(getFishUserJackpotKey(), String.valueOf(existBalance));
        return existBalance;
    }

    public boolean hit(BigDecimal productPrice, boolean isSure) {
        BigDecimal fishUserJackpot = getFishUserJackpot();
        if (fishUserJackpot.compareTo(productPrice) < 0 && !isSure) {
            return false;
        }
        return true;
    }

    public void updateFishUserJackpot(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getFishUserJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getFishUserJackpotKey(), String.valueOf(newBalance));
        FishUserJackpotBillRecord record = new FishUserJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());

        List<FishUserJackpotBillRecord> records = new ArrayList<>();
        records.add(record);
        this.record(records, "FishUserJackpotListener");
    }

    private String getFishUserJackpotKey() {
        return Messages.format("balance:{}", FISH_USER_JACKPOT_BALANCE);
    }


}
