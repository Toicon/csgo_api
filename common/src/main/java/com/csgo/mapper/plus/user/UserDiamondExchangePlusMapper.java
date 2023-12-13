package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.user.UserDiamondExchangePlus;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface UserDiamondExchangePlusMapper extends BaseMapper<UserDiamondExchangePlus> {

    /**
     * 获取用户记录数
     *
     * @param userId
     * @return
     */
    default int getCountByUserId(Integer userId) {
        LambdaQueryWrapper<UserDiamondExchangePlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserDiamondExchangePlus::getUserId, userId);
        return selectCount(wrapper);
    }
}
