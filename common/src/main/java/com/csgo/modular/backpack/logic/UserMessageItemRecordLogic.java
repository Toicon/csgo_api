package com.csgo.modular.backpack.logic;

import com.csgo.domain.plus.message.UserMessageItemRecord;
import com.csgo.mapper.plus.message.UserMessageItemRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageItemRecordLogic {

    private final UserMessageItemRecordMapper userMessageItemRecordMapper;

    public int add(Integer recordId, Integer messageId, String img) {
        //背包流水详情记录
        UserMessageItemRecord itemRecord = new UserMessageItemRecord();
        itemRecord.setRecordId(recordId);
        itemRecord.setUserMessageId(messageId);
        itemRecord.setImg(img);
        return userMessageItemRecordMapper.insert(itemRecord);
    }

}
