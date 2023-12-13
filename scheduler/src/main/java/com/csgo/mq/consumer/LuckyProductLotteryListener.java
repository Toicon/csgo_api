package com.csgo.mq.consumer;

import static com.csgo.mq.MqConstants.GID_LUCKY_PRODUCT_LOTTERY;
import static com.csgo.mq.MqConstants.T_LOTTERY;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.batch.BatchMessageListener;
import com.csgo.domain.plus.config.LuckyProductDrawRecord;
import com.csgo.domain.plus.jackpot.LuckyProductJackpotBillRecord;
import com.csgo.mq.Group;
import com.csgo.mq.MqMessage;
import com.csgo.mq.Topic;
import com.csgo.service.jackpot.LuckyProductJackpotBillsService;
import com.csgo.service.jackpot.LuckyProductJackpotService;
import com.csgo.service.lottery.LuckyProductDrawRecordService;
import com.echo.framework.support.jackson.json.JSON;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Component
@Topic(T_LOTTERY)
@Group(GID_LUCKY_PRODUCT_LOTTERY)
@Slf4j
public class LuckyProductLotteryListener implements BatchMessageListener {
    @Autowired
    private LuckyProductJackpotBillsService luckyProductJackpotBillsService;
    @Autowired
    private LuckyProductJackpotService luckyProductJackpotService;
    @Autowired
    private LuckyProductDrawRecordService luckyProductDrawRecordService;

    @Override
    public Action consume(List<Message> messages, ConsumeContext context) {
        try {
            List<LuckyProductJackpotBillRecord> luckyProductJackpotBillRecords = new ArrayList<>();
            List<LuckyProductJackpotBillRecord> luckyProductJackpotRecords = new ArrayList<>();
            List<LuckyProductDrawRecord> luckyProductDrawRecords = new ArrayList<>();
            for (Message message : messages) {
                String body = new String(message.getBody());
                log.info("Receive: body {}", body);
                List<MqMessage> mqMessages = JSON.fromJSON(body,new TypeReference<List<MqMessage>>() {
                });
                for (MqMessage mqMessage : mqMessages) {
                    if (MqMessage.Category.LUCKY_PRODUCT.equals(mqMessage.getCategory()) && MqMessage.Type.JACKPOT.equals(mqMessage.getType())) {
                        LuckyProductJackpotBillRecord luckyProductJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), LuckyProductJackpotBillRecord.class);
                        luckyProductJackpotBillRecords.add(luckyProductJackpotBillRecord);
                        luckyProductJackpotRecords.add(luckyProductJackpotBillRecord);
                        continue;
                    }
                    if (MqMessage.Category.LUCKY_PRODUCT.equals(mqMessage.getCategory()) && MqMessage.Type.LOG.equals(mqMessage.getType())) {
                        luckyProductDrawRecords.add(JSON.fromJSON(mqMessage.getBody(), LuckyProductDrawRecord.class));
                    }
                }
            }
            luckyProductJackpotBillsService.record(luckyProductJackpotBillRecords);
            luckyProductJackpotService.record(luckyProductJackpotRecords, "JackpotListener");
            luckyProductDrawRecordService.batchInsert(luckyProductDrawRecords);
            //do something..
            return Action.CommitMessage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //消费失败
            return Action.ReconsumeLater;
        }
    }
}
