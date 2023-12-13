package com.csgo.service;

import com.csgo.domain.plus.message.UserMessageRecord;
import com.csgo.domain.report.StatisticsDTO;
import com.csgo.mapper.plus.message.UserMessageRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AdminUserService adminUserService;

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

    public UserMessageRecord queryById(int id) {
        return mapper.selectById(id);
    }

    public List<UserMessageRecord> queryAll() {
        return mapper.selectList(null);
    }

    /**
     * 当日开箱次数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<StatisticsDTO> countOpenCount(String startDate, String endDate) {
        String dataScope = adminUserService.getUserDataScope("adminUser");
        return mapper.countOpenCount(startDate, endDate, dataScope);
    }

    /**
     * 活跃用户，当日开过一次箱子的用户
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<StatisticsDTO> countActiveCount(String startDate, String endDate) {
        String dataScope = adminUserService.getUserDataScope("adminUser");
        return mapper.countActiveCount(startDate, endDate, dataScope);
    }
}
