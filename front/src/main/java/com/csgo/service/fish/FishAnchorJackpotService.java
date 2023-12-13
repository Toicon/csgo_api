package com.csgo.service.fish;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.fish.FishAnchorJackpot;
import com.csgo.domain.plus.fish.FishAnchorJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.fish.FishAnchorJackpotMapper;
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
public class FishAnchorJackpotService {
    private static final String FISH_ANCHOR_JACKPOT_BALANCE = "FISH_ANCHOR_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private FishAnchorJackpotMapper fishAnchorJackpotMapper;
    @Autowired
    private SystemJackpotMessageProducer messageProducer;

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
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.FISH, MqMessage.Type.FISH_ANCHOR_JACKPOT, JSON.toJSON(record))));
    }


    public boolean hit(BigDecimal productPrice, boolean isSure) {
        BigDecimal boxJackpot = getFishAnchorJackpot();
        if (boxJackpot.compareTo(productPrice) < 0 && !isSure) {
            return false;
        }
        return true;
    }


    private String getFishAnchorJackpotKey() {
        return Messages.format("balance:{}", FISH_ANCHOR_JACKPOT_BALANCE);
    }


}
