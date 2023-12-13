package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.domain.plus.jackpot.LuckyProductJackpot;
import com.csgo.domain.plus.jackpot.LuckyProductJackpotBillRecord;
import com.csgo.mapper.plus.jackpot.LuckyProductJackpotMapper;

/**
 * @author admin
 */
@Service
public class LuckyProductJackpotService {

    @Autowired
    private LuckyProductJackpotMapper jackpotMapper;

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
}
