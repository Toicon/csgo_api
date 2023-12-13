package com.csgo.service.user;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.user.SteamUpdateType;
import com.csgo.domain.plus.user.UserSteamUpdateRecord;
import com.csgo.mapper.plus.user.UserSteamUpdateRecordMapper;
import com.csgo.util.DateUtilsEx;

/**
 * @author admin
 */
@Service
public class UserSteamUpdateRecordService {

    @Autowired
    private UserSteamUpdateRecordMapper userSteamUpdateRecordMapper;

    @Transactional
    public void add(int userId, String before, String after, SteamUpdateType steamUpdateType, String source) {
        UserSteamUpdateRecord record = new UserSteamUpdateRecord();
        record.setUserId(userId);
        record.setBeforeSteam(before);
        record.setAfterSteam(after);
        record.setSteamUpdateType(steamUpdateType);
        BaseEntity.created(record, source);
        userSteamUpdateRecordMapper.insert(record);
    }

    public List<UserSteamUpdateRecord> findByUserAndDate(int userId) {
        Date date = new Date();
        return userSteamUpdateRecordMapper.findByUserAndDate(DateUtilsEx.toDayStart(date), DateUtilsEx.toDayEnd(date), userId);
    }
}
