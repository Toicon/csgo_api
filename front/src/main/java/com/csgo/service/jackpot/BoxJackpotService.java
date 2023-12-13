package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.Arrays;

import com.csgo.support.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.BoxJackpot;
import com.csgo.domain.plus.jackpot.BoxJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.jackpot.BoxJackpotMapper;
import com.csgo.mq.MqMessage;
import com.csgo.mq.producer.LotteryMessageProducer;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.Messages;

/**
 * @author admin
 */
@Service
public class BoxJackpotService {
    private static final String BOX_JACKPOT_BALANCE = "BOX_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private BoxJackpotMapper boxJackpotMapper;
    @Autowired
    private LotteryMessageProducer messageProducer;

    public BigDecimal getBoxJackpotBalance() {
        BoxJackpot jackpot = boxJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    public BigDecimal getBoxJackpot() {
        String balance = redisTemplateFacde.get(getBoxJackpotKey());
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = getBoxJackpotBalance();
        redisTemplateFacde.set(getBoxJackpotKey(), String.valueOf(existBalance));
        return existBalance;
    }

    public void updateBoxJackpotKey(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getBoxJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getBoxJackpotKey(), String.valueOf(newBalance));
        BoxJackpotBillRecord record = new BoxJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.BOX_JACKPOT_KEY, JSON.toJSON(record))));
    }

    public void updateBoxJackpot(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getBoxJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getBoxJackpotKey(), String.valueOf(newBalance));
        BoxJackpotBillRecord record = new BoxJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.BOX_JACKPOT, JSON.toJSON(record))));
    }

    public boolean preHit(BigDecimal productPrice) {
        BigDecimal boxJackpot = getBoxJackpot();
        return boxJackpot.compareTo(productPrice) >= 0;
    }

    public boolean hit(BigDecimal productPrice, UserPlus player, boolean isSure) {
        BigDecimal boxJackpot = getBoxJackpot();
        if (boxJackpot.compareTo(productPrice) < 0 && !isSure) {
            return false;
        }
        if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
            updateBoxJackpot(productPrice.multiply(new BigDecimal(-1)), player);
        }
        return true;
    }


    private String getBoxJackpotKey() {
        return Messages.format("balance:{}", BOX_JACKPOT_BALANCE);
    }


}
