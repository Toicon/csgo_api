package com.csgo.modular.backpack.logic;

import com.csgo.domain.plus.message.UserMessageRecord;
import com.csgo.mapper.plus.message.UserMessageRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * UserMessageRecordService
 *
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageRecordLogic {

    private final UserMessageRecordMapper userMessageRecordMapper;

    public static final String IN = "IN";
    public static final String OUT = "OUT";

    @Transactional(rollbackFor = Exception.class)
    public int inPackage(Integer userId, String source) {
        return add(userId, source, IN);
    }

    @Transactional(rollbackFor = Exception.class)
    public int outPackage(Integer userId, String source) {
        return add(userId, source, OUT);
    }

    @Transactional(rollbackFor = Exception.class)
    public int add(Integer userId, String source, String operation) {
        UserMessageRecord record = new UserMessageRecord();
        record.setCt(new Date());
        record.setNum(String.valueOf(System.currentTimeMillis()));
        record.setUserId(userId);
        record.setSource(source);
        record.setOperation(operation);
        userMessageRecordMapper.insert(record);
        return record.getId();
    }

}
