package com.csgo.mq.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.batch.BatchMessageListener;
import com.csgo.domain.plus.anchor.BoxAnchorJackpotBillRecord;
import com.csgo.domain.plus.jackpot.BattleJackpotBillRecord;
import com.csgo.domain.plus.jackpot.BoxJackpotBillRecord;
import com.csgo.domain.plus.jackpot.JackpotBillRecord;
import com.csgo.domain.plus.jackpot.UpgradeJackpotBillRecord;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.domain.plus.mine.MineJackpotBillRecord;
import com.csgo.mq.Group;
import com.csgo.mq.MqMessage;
import com.csgo.mq.Topic;
import com.csgo.service.jackpot.*;
import com.csgo.service.lottery.LotteryDrawRecordService;
import com.echo.framework.support.jackson.json.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.csgo.mq.MqConstants.GID_LOTTERY;
import static com.csgo.mq.MqConstants.T_LOTTERY;

/**
 * @author admin
 */
@Component
@Topic(T_LOTTERY)
@Group(GID_LOTTERY)
@Slf4j
public class LotteryListener implements BatchMessageListener {
    @Autowired
    private JackpotService jackpotService;
    @Autowired
    private JackpotBillsService jackpotBillsService;
    @Autowired
    private LotteryDrawRecordService lotteryDrawRecordService;
    @Autowired
    private BoxJackpotService boxJackpotService;
    @Autowired
    private UpgradeJackpotService upgradeJackpotService;
    @Autowired
    private BattleJackpotService battleJackpotService;
    @Autowired
    private MineJackpotService mineJackpotService;
    @Autowired
    private BoxAnchorJackpotService boxAnchorJackpotService;

    @Override
    public Action consume(List<Message> messages, ConsumeContext context) {
        try {
            List<JackpotBillRecord> jackpotBillRecords = new ArrayList<>();
            List<JackpotBillRecord> jackpotRecords = new ArrayList<>();
            List<BoxJackpotBillRecord> boxJackpotBillRecords = new ArrayList<>();
            List<UpgradeJackpotBillRecord> upgradeJackpotBillRecords = new ArrayList<>();
            List<BattleJackpotBillRecord> battleJackpotBillRecords = new ArrayList<>();
            List<LotteryDrawRecord> lotteryDrawRecords = new ArrayList<>();
            List<MineJackpotBillRecord> mineJackpotBillRecords = new ArrayList<>();
            List<BoxAnchorJackpotBillRecord> boxAnchorJackpotBillRecords = new ArrayList<>();
            List<BoxJackpotBillRecord> boxJackpotBillRecordsKeyList = new ArrayList<>();
            List<BoxAnchorJackpotBillRecord> boxAnchorJackpotBillRecordsKeyList = new ArrayList<>();

            for (Message message : messages) {
                String body = new String(message.getBody());
                log.info("Receive: body {}", body);
                List<MqMessage> mqMessages = JSON.fromJSON(body, new TypeReference<List<MqMessage>>() {
                });
                for (MqMessage mqMessage : mqMessages) {
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.JACKPOT.equals(mqMessage.getType())) {
                        JackpotBillRecord jackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), JackpotBillRecord.class);
                        jackpotBillRecords.add(jackpotBillRecord);
                        jackpotRecords.add(JSON.fromJSON(mqMessage.getBody(), JackpotBillRecord.class));
                        continue;
                    }
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.LOG.equals(mqMessage.getType())) {
                        lotteryDrawRecords.addAll(JSON.fromJSON(mqMessage.getBody(), new TypeReference<List<LotteryDrawRecord>>() {
                        }));
                        continue;
                    }
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.BOX_JACKPOT.equals(mqMessage.getType())) {
                        BoxJackpotBillRecord boxJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), BoxJackpotBillRecord.class);
                        boxJackpotBillRecords.add(boxJackpotBillRecord);
                        continue;
                    }
                    // 开箱全额
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.BOX_JACKPOT_KEY.equals(mqMessage.getType())) {
                        BoxJackpotBillRecord boxJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), BoxJackpotBillRecord.class);
                        boxJackpotBillRecordsKeyList.add(boxJackpotBillRecord);
                        continue;
                    }
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.BATTLE_JACKPOT.equals(mqMessage.getType())) {
                        BattleJackpotBillRecord battleJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), BattleJackpotBillRecord.class);
                        battleJackpotBillRecords.add(battleJackpotBillRecord);
                        continue;
                    }
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.UPGRADE_JACKPOT.equals(mqMessage.getType())) {
                        UpgradeJackpotBillRecord upgradeJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), UpgradeJackpotBillRecord.class);
                        upgradeJackpotBillRecords.add(upgradeJackpotBillRecord);
                        continue;
                    }
                    //扫雷玩法
                    if (MqMessage.Category.MINE.equals(mqMessage.getCategory()) && MqMessage.Type.MINE_JACKPOT.equals(mqMessage.getType())) {
                        MineJackpotBillRecord mineJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), MineJackpotBillRecord.class);
                        mineJackpotBillRecords.add(mineJackpotBillRecord);
                        continue;
                    }
                    //测试账号开箱
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.BOX_ANCHOR_JACKPOT.equals(mqMessage.getType())) {
                        BoxAnchorJackpotBillRecord boxAnchorJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), BoxAnchorJackpotBillRecord.class);
                        boxAnchorJackpotBillRecords.add(boxAnchorJackpotBillRecord);
                        continue;
                    }
                    // 开箱全额
                    if (MqMessage.Category.LOTTERY.equals(mqMessage.getCategory()) && MqMessage.Type.BOX_ANCHOR_JACKPOT_KEY.equals(mqMessage.getType())) {
                        BoxAnchorJackpotBillRecord boxAnchorJackpotBillRecord = JSON.fromJSON(mqMessage.getBody(), BoxAnchorJackpotBillRecord.class);
                        boxAnchorJackpotBillRecordsKeyList.add(boxAnchorJackpotBillRecord);
                        continue;
                    }
                }
            }
            jackpotBillsService.record(jackpotBillRecords);
            jackpotService.record(jackpotRecords, "JackpotListener");
            // 开箱
            boxJackpotService.record(boxJackpotBillRecords, "BoxJackpotListener");
            boxJackpotService.recordKey(boxJackpotBillRecordsKeyList, "BoxJackpotListenerV2");
            upgradeJackpotService.record(upgradeJackpotBillRecords, "UpgradeJackpotListener");
            battleJackpotService.record(battleJackpotBillRecords, "BattleJackpotListener");
            lotteryDrawRecordService.batchInsert(lotteryDrawRecords);
            mineJackpotService.record(mineJackpotBillRecords, "MineJackpotListener");
            // 开箱
            boxAnchorJackpotService.record(boxAnchorJackpotBillRecords, "BoxAnchorJackpotListener");
            boxAnchorJackpotService.recordKey(boxAnchorJackpotBillRecordsKeyList, "BoxAnchorJackpotListenerV2");
            //do something..
            return Action.CommitMessage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //消费失败
            return Action.ReconsumeLater;
        }
    }
}
