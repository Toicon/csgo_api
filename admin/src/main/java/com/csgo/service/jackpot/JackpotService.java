package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.csgo.domain.plus.jackpot.Jackpot;
import com.csgo.domain.plus.jackpot.JackpotBillRecord;
import com.csgo.mapper.plus.jackpot.JackpotMapper;

/**
 * @author admin
 */
@Service
public class JackpotService {

    private static final String JACKPOT_BALANCE_REDIS_KEY = "JACKPOT_BALANCE";

    @Autowired
    private JackpotMapper jackpotMapper;

    @Cacheable(cacheNames = JACKPOT_BALANCE_REDIS_KEY)
    public BigDecimal getBalance() {
        Jackpot jackpot = jackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    @CacheEvict(cacheNames = JACKPOT_BALANCE_REDIS_KEY)
    @Transactional
    public void record(List<JackpotBillRecord> records, String operation) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        BigDecimal income = records.stream().map(JackpotBillRecord::getIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
        Jackpot jackpot = jackpotMapper.selectOne(null);
        if (jackpot == null) {
            jackpot = new Jackpot();
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

    @CacheEvict(cacheNames = JACKPOT_BALANCE_REDIS_KEY)
    @Transactional
    public void update(BigDecimal balance, String operation) {
        Jackpot jackpot = jackpotMapper.selectOne(null);
        if (jackpot == null) {
            jackpot = new Jackpot();
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
