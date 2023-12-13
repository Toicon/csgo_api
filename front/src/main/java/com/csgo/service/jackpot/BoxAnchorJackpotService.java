package com.csgo.service.jackpot;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.anchor.BoxAnchorJackpot;
import com.csgo.domain.plus.anchor.BoxAnchorJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.anchor.BoxAnchorJackpotMapper;
import com.csgo.mq.MqMessage;
import com.csgo.mq.producer.LotteryMessageProducer;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author admin
 */
@Service
public class BoxAnchorJackpotService {
    private static final String BOX_ANCHOR_JACKPOT_BALANCE = "BOX_ANCHOR_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private BoxAnchorJackpotMapper boxAnchorJackpotMapper;
    @Autowired
    private LotteryMessageProducer messageProducer;

    public BigDecimal getBoxAnchorJackpotBalance() {
        BoxAnchorJackpot jackpot = boxAnchorJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    public BigDecimal getBoxAnchorJackpot() {
        String balance = redisTemplateFacde.get(getBoxAnchorJackpotKey());
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = getBoxAnchorJackpotBalance();
        redisTemplateFacde.set(getBoxAnchorJackpotKey(), String.valueOf(existBalance));
        return existBalance;
    }

    public void updateBoxAnchorJackpotKey(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getBoxAnchorJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getBoxAnchorJackpotKey(), String.valueOf(newBalance));
        BoxAnchorJackpotBillRecord record = new BoxAnchorJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.BOX_ANCHOR_JACKPOT_KEY, JSON.toJSON(record))));
    }

    public void updateBoxAnchorJackpot(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getBoxAnchorJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getBoxAnchorJackpotKey(), String.valueOf(newBalance));
        BoxAnchorJackpotBillRecord record = new BoxAnchorJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.BOX_ANCHOR_JACKPOT, JSON.toJSON(record))));
    }

    public boolean preHit(BigDecimal productPrice) {
        BigDecimal boxJackpot = getBoxAnchorJackpot();
        return boxJackpot.compareTo(productPrice) >= 0;
    }

    public boolean hit(BigDecimal productPrice, UserPlus player, boolean isSure) {
        BigDecimal boxJackpot = getBoxAnchorJackpot();
        if (boxJackpot.compareTo(productPrice) < 0 && !isSure) {
            return false;
        }
        updateBoxAnchorJackpot(productPrice.multiply(new BigDecimal(-1)), player);
        return true;
    }


    private String getBoxAnchorJackpotKey() {
        return Messages.format("balance:{}", BOX_ANCHOR_JACKPOT_BALANCE);
    }


}
