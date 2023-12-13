package com.csgo.service.envelop;

import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.framework.service.LockService;
import com.csgo.service.OrderRecordService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
public class RedEnvelopReceiveService {

    @Autowired
    private LockService lockService;

    @Autowired
    private RedEnvelopItemService redEnvelopItemService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private RedEnvelopRecordService redEnvelopRecordService;

    @Transactional(rollbackFor = Exception.class)
    public void receiptedEnvelop(Integer userId, Integer envelopId) {
        RedEnvelopItem item = redEnvelopItemService.getByEnvelopId(envelopId);
        if (item == null) {
            log.warn("[一键领取] 红包不存在,userId:{} envelopId:{}", userId, envelopId);
            return;
        }

        //活动时间判断
        Date date = new Date();
        if (item.getEffectiveStartTime().after(date)) {
            log.warn("[一键领取] 当前活动红包未开始,userId:{} envelopId:{}", userId, envelopId);
            return;
        }
        if (item.getEffectiveEndTime().before(date)) {
            log.warn("[一键领取] userId:{} envelopId:{} 当前活动红包已结束", userId, envelopId);
            return;
        }

        String lockKey = "envelopItemId:" + item.getId();
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                log.warn("[一键领取] userId:{} envelopId:{} 获取锁失败", userId, envelopId);
                return;
            }

            //充值条件判断
            List<OrderRecord> orderRecords = orderRecordService.findRecharge(userId, item.getEffectiveStartTime(), item.getEffectiveEndTime(), "2");
            BigDecimal rechargeAmount = orderRecords.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (item.getLimitAmount().compareTo(rechargeAmount) > 0) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("参与失败，再充值")
                        .append(item.getLimitAmount().subtract(rechargeAmount))
                        .append("芯片参与");

                log.warn("[一键领取] userId:{} envelopId:{} amount:{} msg:{}", userId, envelopId, rechargeAmount, stringBuffer);
                return;
            }

            BigDecimal amount = BigDecimal.ZERO;
            //领取条件判断
            List<RedEnvelopRecord> records = redEnvelopRecordService.findReceive(userId, item.getEnvelopId(), item.getEffectiveStartTime());
            if (!CollectionUtils.isEmpty(records)) {
                log.warn("[一键领取] userId:{} envelopId:{} 您已领取过红包了", userId, envelopId);
                return;
            }
            //红包剩余判断
            int receiveCount = redEnvelopRecordService.getReceiveCount(item.getId());
            if (receiveCount >= item.getNum()) {
                log.warn("[一键领取] userId:{} envelopId:{} 红包剩余数量不足，请稍后再试。", userId, envelopId);
                return;
            }
            double minAmount = item.getMinAmount().doubleValue();
            if (item.getMinAmount().compareTo(BigDecimal.ZERO) <= 0) {
                minAmount = 0.01;
            }
            double randomAmount = ThreadLocalRandom.current().nextDouble(minAmount, item.getMaxAmount().doubleValue());
            amount = BigDecimal.valueOf(randomAmount).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal lastAmount = new BigDecimal("0.01");
            if (amount.compareTo(lastAmount) < 0) {
                log.info("[一键领取] userId:{} envelopId:{} red envelop random amount :{} amount:{}", userId, envelopId, randomAmount, amount);
                amount = lastAmount;
            }
            redEnvelopRecordService.insert(userId, amount, item.getId());
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

}
