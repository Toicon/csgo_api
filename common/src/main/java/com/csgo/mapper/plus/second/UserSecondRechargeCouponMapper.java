package com.csgo.mapper.plus.second;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.second.UserSecondRechargeCoupon;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 用户二次充值优惠券
 */
@Repository
public interface UserSecondRechargeCouponMapper extends BaseMapper<UserSecondRechargeCoupon> {

    /**
     * 获取用户二次充值优惠券
     *
     * @param userId
     * @return
     */
    default UserSecondRechargeCoupon getEffectiveByUserId(Integer userId) {
        LambdaQueryWrapper<UserSecondRechargeCoupon> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserSecondRechargeCoupon::getUserId, userId);
        wrapper.ge(UserSecondRechargeCoupon::getFailureTime, new Date());
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }
}
