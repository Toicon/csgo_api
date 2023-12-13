package com.csgo.service.message;

import com.csgo.domain.plus.blind.BlindBoxTurnPlus;
import com.csgo.domain.plus.message.UserMessageItemRecord;
import com.csgo.domain.plus.message.UserMessageRecord;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.mapper.plus.message.UserMessageItemRecordMapper;
import com.csgo.mapper.plus.message.UserMessageRecordMapper;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPrizePlusMapper;
import com.csgo.support.GlobalConstants;
import com.csgo.util.GeneralConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class UserMessageHandlerService {

    @Autowired
    private UserMessageRecordMapper mapper;
    @Autowired
    private UserMessageItemRecordMapper userMessageItemRecordMapper;
    @Autowired
    private UserMessagePlusMapper userMessageMapper;
    @Autowired
    private UserMessageGiftPlusMapper userMessageGiftMapper;
    @Autowired
    private UserPrizePlusMapper userPrizePlusMapper;

    @Transactional
    public void blindRecord(UserPlus user, List<BlindBoxTurnPlus> blindBoxTurns) {
        int recordId = add(user.getId(), "盲盒挑战", "IN");
        blindBoxTurns.forEach(item -> {
            //增加用户背包信息
            UserMessagePlus message = new UserMessagePlus();
            message.setGameName("盲盒挑战");
            message.setGiftProductId(item.getProductId());
            message.setGiftType("盲盒挑战");
            message.setMoney(item.getPrice());
            message.setDrawDare(new Date());
            message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
            message.setImg(item.getImgUrl());
            message.setUserId(user.getId());
            message.setProductName(item.getProductName());
            message.setGiftStatus("0");
            message.setTurnId(item.getId());
            userMessageMapper.insert(message);

            //背包流水详情记录
            add(recordId, message.getId(), message.getImg());

            //抽奖入库记录
            UserMessageGiftPlus userMessageGift = new UserMessageGiftPlus();
            userMessageGift.setCt(new Date());
            userMessageGift.setUserMessageId(message.getId());
            userMessageGift.setGiftProductId(item.getProductId());
            userMessageGift.setUserId(user.getId());
            userMessageGift.setImg(item.getImgUrl());
            userMessageGift.setPhone(user.getUserName());
            userMessageGift.setState(0);
            userMessageGift.setMoney(item.getPrice());
            userMessageGift.setUt(new Date());
            userMessageGift.setGameName("盲盒挑战");
            userMessageGift.setGiftProductName(item.getProductName());
            userMessageGift.setGiftType("盲盒挑战");
            userMessageGiftMapper.insert(userMessageGift);

            //增加用户的中奖记录
            UserPrizePlus userPrize = new UserPrizePlus();
            userPrize.setUserId(Integer.valueOf(user.getId() + ""));
            String userName = user.getName();
            userPrize.setUserNameQ(userName);
            String phoneNumber = userName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            userPrize.setUserName(phoneNumber);
            userPrize.setGameName("盲盒挑战");
            userPrize.setGiftId(item.getBlindBoxId());
            userPrize.setGiftProductId(item.getProductId());
            userPrize.setGiftProductName(item.getProductName());
            userPrize.setGiftType("盲盒挑战");
            userPrize.setGiftProductImg(item.getImgUrl());
            userPrize.setPrice(item.getPrice());
            userPrize.setGiftName("盲盒挑战");
            userPrize.setGiftGradeG(String.valueOf(GeneralConfigUtil.giftGradeG(item.getPrice())));
            userPrize.setGiftPrice(item.getPrice());
            userPrize.setCt(new Date());
            userPrizePlusMapper.insert(userPrize);
        });
    }

    private int add(Integer userId, String source, String operation) {
        UserMessageRecord record = new UserMessageRecord();
        record.setNum(String.valueOf(System.currentTimeMillis()));
        record.setUserId(userId);
        record.setCt(new Date());
        record.setSource(source);
        record.setOperation(operation);
        mapper.insert(record);
        return record.getId();
    }

    private int add(Integer recordId, Integer messageId, String img) {
        //背包流水详情记录
        UserMessageItemRecord itemRecord = new UserMessageItemRecord();
        itemRecord.setRecordId(recordId);
        itemRecord.setUserMessageId(messageId);
        itemRecord.setImg(img);
        return userMessageItemRecordMapper.insert(itemRecord);
    }
}
