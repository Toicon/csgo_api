package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.user.UserPlatformRewardRecordDO;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface UserPlatformRewardRecordMapper extends BaseMapper<UserPlatformRewardRecordDO> {

    default UserPlatformRewardRecordDO selectByTypeAndUserId(Integer type, Integer userId) {
        return selectOne(new LambdaQueryWrapper<UserPlatformRewardRecordDO>()
                .eq(UserPlatformRewardRecordDO::getType, type)
                .eq(UserPlatformRewardRecordDO::getUserId, userId)
                .orderByDesc(UserPlatformRewardRecordDO::getId)
                .last("limit 1")
        );
    }
}
