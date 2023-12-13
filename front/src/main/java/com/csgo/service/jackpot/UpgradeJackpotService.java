package com.csgo.service.jackpot;

import java.math.BigDecimal;
import java.util.Arrays;

import com.csgo.support.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.jackpot.UpgradeJackpot;
import com.csgo.domain.plus.jackpot.UpgradeJackpotBillRecord;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.jackpot.UpgradeJackpotMapper;
import com.csgo.mq.MqMessage;
import com.csgo.mq.producer.LotteryMessageProducer;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.Messages;

/**
 * @author admin
 */
@Service
public class UpgradeJackpotService {
    private static final String UPGRADE_JACKPOT_BALANCE = "UPGRADE_JACKPOT_BALANCE";
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private UpgradeJackpotMapper upgradeJackpotMapper;
    @Autowired
    private LotteryMessageProducer messageProducer;

    public BigDecimal getUpgradeJackpotBalance() {
        UpgradeJackpot jackpot = upgradeJackpotMapper.selectOne(null);
        if (jackpot == null) {
            return BigDecimal.ZERO;
        }
        return jackpot.getBalance();
    }

    public BigDecimal getUpgradeJackpot() {
        String balance = redisTemplateFacde.get(getUpgradeJackpotKey());
        if (StringUtils.hasText(balance)) {
            return new BigDecimal(balance);
        }
        BigDecimal existBalance = getUpgradeJackpotBalance();
        redisTemplateFacde.set(getUpgradeJackpotKey(), String.valueOf(existBalance));
        return existBalance;
    }

    public void updateUpgradeJackpot(BigDecimal amount, UserPlus player) {
        BigDecimal stock = amount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal balance = getUpgradeJackpot();
        BigDecimal newBalance = balance.add(stock);
        redisTemplateFacde.set(getUpgradeJackpotKey(), String.valueOf(newBalance));
        UpgradeJackpotBillRecord record = new UpgradeJackpotBillRecord();
        record.setBalance(newBalance);
        record.setAmount(stock);
        record.setUserId(player.getId());
        record.setPhone(player.getUserName());
        record.setUserName(player.getName());
        BaseEntity.created(record, player.getUserName());
        messageProducer.record(Arrays.asList(new MqMessage(MqMessage.Category.LOTTERY, MqMessage.Type.UPGRADE_JACKPOT, JSON.toJSON(record))));
    }

    public boolean hit(BigDecimal productPrice, UserPlus player, boolean isHit) {
        if (!isHit) {
            return false;
        }
        if (GlobalConstants.RETAIL_USER_FLAG == player.getFlag()) {
            BigDecimal upgradeJackpot = getUpgradeJackpot();
            if (upgradeJackpot.compareTo(productPrice) < 0) {
                return false;
            }
            updateUpgradeJackpot(productPrice.multiply(new BigDecimal(-1)), player);
        }
        return true;
    }


    private String getUpgradeJackpotKey() {
        return Messages.format("balance:{}", UPGRADE_JACKPOT_BALANCE);
    }


}
