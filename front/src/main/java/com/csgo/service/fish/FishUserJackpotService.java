package com.csgo.service.fish;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.fish.FishUserJackpot;
import com.csgo.domain.plus.fish.FishUserJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.fish.FishUserJackpotMapper;
import com.csgo.modular.system.mq.SystemJackpotMessageProducer;
import com.csgo.mq.MqMessage;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;


@Service
public class FishUserJackpotService {

    private static final String FISH_USER_JACKPOT_BALANCE = "FISH_USER_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private FishUserJackpotMapper fishUserJackpotMapper;
    @Autowired
    private SystemJackpotMessageProducer messageProducer;

    public BigDecimal getFishUserJackpotBalance() {
        FishUserJackpot jackpot = fishUserJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
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
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.FISH, MqMessage.Type.FISH_USER_JACKPOT, JSON.toJSON(record))));
    }

    public boolean hit(BigDecimal productPrice, boolean isSure) {
        BigDecimal fishUserJackpot = getFishUserJackpot();
        if (fishUserJackpot.compareTo(productPrice) < 0 && !isSure) {
            return false;
        }
        return true;
    }


    private String getFishUserJackpotKey() {
        return Messages.format("balance:{}", FISH_USER_JACKPOT_BALANCE);
    }


}
