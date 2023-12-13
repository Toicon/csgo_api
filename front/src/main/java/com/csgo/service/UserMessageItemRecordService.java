package com.csgo.service;

import com.csgo.domain.plus.message.UserMessageItemRecord;
import com.csgo.domain.plus.message.UserMessageItemRecordDTO;
import com.csgo.mapper.plus.message.UserMessageItemRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class UserMessageItemRecordService {

    @Autowired
    private UserMessageItemRecordMapper mapper;


    public int add(Integer recordId, Integer messageId, String img) {
        //背包流水详情记录
        UserMessageItemRecord itemRecord = new UserMessageItemRecord();
        itemRecord.setRecordId(recordId);
        itemRecord.setUserMessageId(messageId);
        itemRecord.setImg(img);
        return mapper.insert(itemRecord);
    }

    public UserMessageItemRecord get(int id) {
        return mapper.selectById(id);
    }

    public List<UserMessageItemRecord> findByRecordIds(List<Integer> userMessageRecordIds) {
        if (CollectionUtils.isEmpty(userMessageRecordIds)) {
            return new ArrayList<>();
        }
        return mapper.findByRecordIds(userMessageRecordIds);
    }

    public List<UserMessageItemRecordDTO> findWithProductByRecordIds(List<Integer> userMessageRecordIds) {
        if (CollectionUtils.isEmpty(userMessageRecordIds)) {
            return new ArrayList<>();
        }
        return mapper.findWithProductByRecordIds(userMessageRecordIds);
    }
}
