package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JackpotMapper jackpotMapper;


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

}
