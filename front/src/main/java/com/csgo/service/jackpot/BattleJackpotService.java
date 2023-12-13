package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.BattleJackpot;
import com.csgo.domain.plus.jackpot.BattleJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.jackpot.BattleJackpotMapper;
import com.csgo.mq.MqMessage;
import com.csgo.mq.producer.LotteryMessageProducer;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.Messages;

/**
 * @author admin
 */
@Service
public class BattleJackpotService {
    private static final String BATTLE_JACKPOT_BALANCE = "BATTLE_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private BattleJackpotMapper battleJackpotMapper;
    @Autowired
    private LotteryMessageProducer messageProducer;

    public BigDecimal getBattleJackpotBalance() {
        BattleJackpot jackpot = battleJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    public BigDecimal getBattleJackpot() {
        String balance = redisTemplateFacde.get(getBattleJackpotKey());
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = getBattleJackpotBalance();
        redisTemplateFacde.set(getBattleJackpotKey(), String.valueOf(existBalance));
        return existBalance;
    }

    public void updateBattleJackpot(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getBattleJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getBattleJackpotKey(), String.valueOf(newBalance));
        BattleJackpotBillRecord record = new BattleJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.BATTLE_JACKPOT, JSON.toJSON(record))));
    }

    public boolean hit(BigDecimal productPrice, UserPlus player, boolean isSure) {
        BigDecimal battleJackpot = getBattleJackpot();
        if (battleJackpot.compareTo(productPrice) < 0 && !isSure) {
            return false;
        }
        updateBattleJackpot(productPrice.multiply(new BigDecimal(-1)), player);
        return true;
    }


    private String getBattleJackpotKey() {
        return Messages.format("balance:{}", BATTLE_JACKPOT_BALANCE);
    }


}
