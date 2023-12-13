package com.csgo.service.mine;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.mine.MineJackpot;
import com.csgo.domain.plus.mine.MineJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.mine.MineJackpotMapper;
import com.csgo.mq.MqMessage;
import com.csgo.mq.producer.MineMessageProducer;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.support.GlobalConstants;
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
public class MineJackpotService {
    private static final String MINE_JACKPOT_BALANCE = "MINE_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private MineJackpotMapper mineJackpotMapper;
    @Autowired
    private MineMessageProducer mineMessageProducer;

    public BigDecimal getMineJackpotBalance() {
        MineJackpot jackpot = mineJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    public BigDecimal getMineJackpot() {
        String balance = redisTemplateFacde.get(getMineJackpotKey());
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = getMineJackpotBalance();
        redisTemplateFacde.set(getMineJackpotKey(), String.valueOf(existBalance));
        return existBalance;
    }

    public void updateMineJackpot(BigDecimal amount, UserPlus player) {
        //测试账号不记录库存
        if (GlobalConstants.INTERNAL_USER_FLAG == player.getFlag()) {
            return;
        }
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getMineJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getMineJackpotKey(), String.valueOf(newBalance));
        MineJackpotBillRecord record = new MineJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setUserName(player.getUserName());
        record.setName(player.getName());
        BaseEntity.created(record, player.getUserName());
        mineMessageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.MINE, MqMessage.Type.MINE_JACKPOT, JSON.toJSON(record))));
    }


    private String getMineJackpotKey() {
        return Messages.format("balance:{}", MINE_JACKPOT_BALANCE);
    }


}
