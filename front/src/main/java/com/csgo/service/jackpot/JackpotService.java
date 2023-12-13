package com.csgo.service.jackpot;

import com.csgo.domain.plus.jackpot.Jackpot;
import com.csgo.domain.plus.jackpot.LuckyProductJackpot;
import com.csgo.mapper.plus.jackpot.JackpotMapper;
import com.csgo.mapper.plus.jackpot.LuckyProductJackpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Service
public class JackpotService {

    private static final String JACKPOT_BALANCE_REDIS_KEY = "JACKPOT_BALANCE";
    private static final String LUCKY_PRODUCT_JACKPOT_BALANCE_REDIS_KEY = "LUCKY_PRODUCT_JACKPOT_BALANCE";

    @Autowired
    private JackpotMapper jackpotMapper;
    @Autowired
    private LuckyProductJackpotMapper luckyProductJackpotMapper;

    @Cacheable(cacheNames = LUCKY_PRODUCT_JACKPOT_BALANCE_REDIS_KEY)
    public BigDecimal getLuckyProductBalance() {
        LuckyProductJackpot jackpot = luckyProductJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    @Cacheable(cacheNames = JACKPOT_BALANCE_REDIS_KEY)
    public BigDecimal getBalance() {
        Jackpot jackpot = jackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }
}
