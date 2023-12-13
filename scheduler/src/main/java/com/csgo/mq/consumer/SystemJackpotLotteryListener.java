package com.csgo.mq.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.batch.BatchMessageListener;
import com.csgo.domain.plus.fish.FishAnchorJackpotBillRecord;
import com.csgo.domain.plus.fish.FishUserJackpotBillRecord;
import com.csgo.modular.bomb.service.system.NovBombJackpotService;
import com.csgo.modular.system.model.dto.SystemJackpotBillRecordDTO;
import com.csgo.modular.tendraw.service.system.TenDrawJackpotService;
import com.csgo.mq.Group;
import com.csgo.mq.MqMessage;
import com.csgo.mq.Topic;
import com.csgo.service.jackpot.FishAnchorJackpotService;
import com.csgo.service.jackpot.FishUserJackpotService;
import com.echo.framework.support.jackson.json.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.csgo.mq.MqConstants.GID_SYSTEM_JACKPOT;
import static com.csgo.mq.MqConstants.T_LOTTERY;

/**
 * @author admin
 */
@Slf4j
@Component
@Topic(T_LOTTERY)
@Group(GID_SYSTEM_JACKPOT)
public class SystemJackpotLotteryListener implements BatchMessageListener {

    @Autowired
    private TenDrawJackpotService tenDrawJackpotService;
    @Autowired
    private NovBombJackpotService novBombJackpotService;

    @Autowired
    private FishUserJackpotService fishUserJackpotService;
    @Autowired
    private FishAnchorJackpotService fishAnchorJackpotService;
    
    @Override
    public Action consume(List<Message> messages, ConsumeContext context) {
        try {
            List<SystemJackpotBillRecordDTO> jackpotRecordList = new ArrayList<>();
            List<SystemJackpotBillRecordDTO> novBombRecordList = new ArrayList<>();
            List<FishUserJackpotBillRecord> fishUserJackpotBillRecords = new ArrayList<>();
            List<FishAnchorJackpotBillRecord> fishAnchorJackpotBillRecords = new ArrayList<>();
            for (Message message : messages) {
                String body = new String(message.getBody());
                log.info("Receive: body {}", body);
                List<MqMessage> mqMessages = JSON.fromJSON(body, new TypeReference<List<MqMessage>>() {
                });
                for (MqMessage mqMessage : mqMessages) {
                    // 系统奖池
                    if (MqMessage.Category.SYSTEM.equals(mqMessage.getCategory()) && MqMessage.Type.TEN_DRAW_JACKPOT.equals(mqMessage.getType())) {
                        SystemJackpotBillRecordDTO record = JSON.fromJSON(mqMessage.getBody(), SystemJackpotBillRecordDTO.class);
                        jackpotRecordList.add(record);
                    } else if (MqMessage.Category.SYSTEM.equals(mqMessage.getCategory()) && MqMessage.Type.NOV_BOMB_JACKPOT.equals(mqMessage.getType())) {
                        //模拟拆弹
                        SystemJackpotBillRecordDTO record = JSON.fromJSON(mqMessage.getBody(), SystemJackpotBillRecordDTO.class);
                        novBombRecordList.add(record);
                    } else if (MqMessage.Category.FISH.equals(mqMessage.getCategory()) && MqMessage.Type.FISH_USER_JACKPOT.equals(mqMessage.getType())) {
                        // 钓鱼散户奖池
                        FishUserJackpotBillRecord record = JSON.fromJSON(mqMessage.getBody(), FishUserJackpotBillRecord.class);
                        fishUserJackpotBillRecords.add(record);
                    } else if (MqMessage.Category.FISH.equals(mqMessage.getCategory()) && MqMessage.Type.FISH_ANCHOR_JACKPOT.equals(mqMessage.getType())) {
                        // 钓鱼测试奖池
                        FishAnchorJackpotBillRecord record = JSON.fromJSON(mqMessage.getBody(), FishAnchorJackpotBillRecord.class);
                        fishAnchorJackpotBillRecords.add(record);
                    }
                }
            }
            tenDrawJackpotService.processRecord(jackpotRecordList);
            novBombJackpotService.processRecord(novBombRecordList);
            fishUserJackpotService.record(fishUserJackpotBillRecords, "FishUserJackpotListener");
            fishAnchorJackpotService.record(fishAnchorJackpotBillRecords, "FishAnchorJackpotListener");
            return Action.CommitMessage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Action.ReconsumeLater;
        }
    }

}
