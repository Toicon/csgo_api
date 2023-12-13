package com.csgo.mapper.plus.user;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.user.UserInnerRechargeLimit;

/**
 * @author admin
 */
@Repository
public interface UserInnerRechargeLimitMapper extends BaseMapper<UserInnerRechargeLimit> {

    default UserInnerRechargeLimit getNotRemoveByUserId(int userId) {
        LambdaQueryWrapper<UserInnerRechargeLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInnerRechargeLimit::getUserId, userId);
        wrapper.eq(UserInnerRechargeLimit::isOvertime, false);
        wrapper.eq(UserInnerRechargeLimit::isWhite, false);
        return selectOne(wrapper);
    }

    default UserInnerRechargeLimit getNotRemoveWhiteByUserId(int userId) {
        LambdaQueryWrapper<UserInnerRechargeLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInnerRechargeLimit::getUserId, userId);
        wrapper.eq(UserInnerRechargeLimit::isOvertime, false);
        wrapper.eq(UserInnerRechargeLimit::isWhite, true);
        return selectOne(wrapper);
    }

    default List<UserInnerRechargeLimit> findNeedRemoveRecord(Date date) {
        LambdaQueryWrapper<UserInnerRechargeLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(UserInnerRechargeLimit::getEndTime, date);
        wrapper.eq(UserInnerRechargeLimit::isOvertime, false);
        wrapper.eq(UserInnerRechargeLimit::isWhite, false);
        return selectList(wrapper);
    }

    default List<UserInnerRechargeLimit> findWhiteList() {
        LambdaQueryWrapper<UserInnerRechargeLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInnerRechargeLimit::isOvertime, false);
        wrapper.eq(UserInnerRechargeLimit::isWhite, true);
        return selectList(wrapper);
    }

}
