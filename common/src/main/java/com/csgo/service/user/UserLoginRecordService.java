package com.csgo.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserLoginRecordCondition;
import com.csgo.domain.plus.user.UserLoginRecord;
import com.csgo.domain.plus.user.UserLoginRecordDTO;
import com.csgo.mapper.plus.user.UserLoginRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Admin on 2021/7/26
 */
@Service
public class UserLoginRecordService {
    @Autowired
    private UserLoginRecordMapper mapper;

    @Transactional
    public void insert(Integer userId, String ip, int port, Boolean isApp) {
        UserLoginRecord record = new UserLoginRecord();
        record.setUserId(userId);
        record.setIp(ip);
        record.setPort(port);
        record.setIsApp(isApp);
        record.setCt(new Date());
        mapper.insert(record);
    }

    /**
     * 分页查询用户登录日志信息
     *
     * @param condition
     */
    public Page<UserLoginRecordDTO> pagination(SearchUserLoginRecordCondition condition) {
        return mapper.pagination(condition.getPage(), condition);
    }
}
