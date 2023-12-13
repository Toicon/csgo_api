package com.csgo.service.message;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.message.UserMessageRecord;
import com.csgo.mapper.plus.message.UserMessageRecordMapper;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class UserMessageRecordService {

    @Autowired
    private UserMessageRecordMapper mapper;


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
}
