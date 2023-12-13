package com.csgo.service.jackpot;

import com.csgo.domain.plus.jackpot.LuckyProductJackpot;
import com.csgo.domain.plus.jackpot.LuckyProductJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.LuckyProductJackpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class LuckyProductJackpotService {

    private static final String LUCKY_PRODUCT_JACKPOT_BALANCE_REDIS_KEY = "LUCKY_PRODUCT_JACKPOT_BALANCE";

    @Autowired
    private LuckyProductJackpotMapper jackpotMapper;

    @Cacheable(cacheNames = LUCKY_PRODUCT_JACKPOT_BALANCE_REDIS_KEY)
    public BigDecimal getBalance() {
        LuckyProductJackpot jackpot = jackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    @CacheEvict(cacheNames = LUCKY_PRODUCT_JACKPOT_BALANCE_REDIS_KEY)
    @Transactional
    public void record(List<LuckyProductJackpotBillRecord> records, String operation) {
        BigDecimal income = records.stream().map(LuckyProductJackpotBillRecord::getIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
        LuckyProductJackpot jackpot = jackpotMapper.selectOne(null);
        if (jackpot == null) {
            jackpot = new LuckyProductJackpot();
            jackpot.setBalance(income);
            jackpot.setUb(operation);
            jackpot.setUt(new Date());
            jackpotMapper.insert(jackpot);
            return;
        }
        jackpot.setBalance(jackpot.getBalance().add(income));
        jackpot.setUb(operation);
        jackpot.setUt(new Date());
        jackpotMapper.updateById(jackpot);
    }

    @CacheEvict(cacheNames = LUCKY_PRODUCT_JACKPOT_BALANCE_REDIS_KEY)
    @Transactional
    public void update(BigDecimal balance, String operation) {
        LuckyProductJackpot jackpot = jackpotMapper.selectOne(null);
        if (jackpot == null) {
            jackpot = new LuckyProductJackpot();
            jackpot.setBalance(balance);
            jackpot.setUb(operation);
            jackpot.setUt(new Date());
            jackpotMapper.insert(jackpot);
            return;
        }
        jackpot.setBalance(balance);
        jackpot.setUb(operation);
        jackpot.setUt(new Date());
        jackpotMapper.updateById(jackpot);
    }
}
