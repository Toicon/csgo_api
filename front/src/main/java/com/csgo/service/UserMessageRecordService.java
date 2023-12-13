package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.SearchUserMessageRecordCondition;
import com.csgo.domain.plus.message.UserMessageItemRecord;
import com.csgo.domain.plus.message.UserMessageRecord;
import com.csgo.mapper.plus.message.UserMessageItemRecordMapper;
import com.csgo.mapper.plus.message.UserMessageRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class UserMessageRecordService {

    @Autowired
    private UserMessageRecordMapper mapper;
    @Autowired
    private UserMessageItemRecordMapper itemRecordMapper;

    @Transactional
    public void delete(Integer recordId) {
        mapper.deleteById(recordId);
    }

    @Transactional
    public int add(Integer userId, String source, String operation) {
        UserMessageRecord record = new UserMessageRecord();
        record.setNum(String.valueOf(System.currentTimeMillis()));
        record.setUserId(userId);
        record.setCt(new Date());
        record.setSource(source);
        record.setOperation(operation);
        mapper.insert(record);
        return record.getId();
    }

    public UserMessageRecord get(int id) {
        return mapper.selectById(id);
    }

    public Page<UserMessageRecord> pagination(SearchUserMessageRecordCondition condition) {
        return mapper.pagination(condition);
    }

    @Transactional
    public void addBatch(Integer userId, String source, String operation, List<UserMessageItemRecord> userMessageItemRecords) {
        int recordId = add(userId, source, operation);
        userMessageItemRecords.forEach(item -> {
            item.setRecordId(recordId);
            itemRecordMapper.insert(item);
        });
    }
}
