package com.csgo.service.user;

import com.csgo.domain.plus.user.UserLoginOutRecord;
import com.csgo.mapper.plus.user.UserLoginOutRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Admin on 2021/7/26
 */
@Service
public class UserLoginOutRecordService {
    @Autowired
    private UserLoginOutRecordMapper mapper;

    @Transactional
    public void insert(Integer userId, String ip, int port, Boolean isApp) {
        UserLoginOutRecord record = new UserLoginOutRecord();
        record.setUserId(userId);
        record.setIp(ip);
        record.setPort(port);
        record.setIsApp(isApp);
        record.setCt(new Date());
        mapper.insert(record);
    }


}
