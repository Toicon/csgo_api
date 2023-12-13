package com.csgo.mapper.plus.user;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.user.SteamUpdateType;
import com.csgo.domain.plus.user.UserSteamUpdateRecord;

/**
 * @author admin
 */
@Repository
public interface UserSteamUpdateRecordMapper extends BaseMapper<UserSteamUpdateRecord> {

    default List<UserSteamUpdateRecord> findByUserAndDate(Date startDate, Date endDate, int userId) {
        LambdaQueryWrapper<UserSteamUpdateRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSteamUpdateRecord::getUserId, userId);
        wrapper.ge(UserSteamUpdateRecord::getCreateDate, startDate);
        wrapper.le(UserSteamUpdateRecord::getCreateDate, endDate);
        wrapper.eq(UserSteamUpdateRecord::getSteamUpdateType, SteamUpdateType.USER);
        return selectList(wrapper);
    }
}
