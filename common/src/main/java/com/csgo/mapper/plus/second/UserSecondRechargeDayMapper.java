package com.csgo.mapper.plus.second;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.second.UserSecondRechargeDay;
import com.csgo.util.DateUtils;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户每日开箱累计
 */
@Repository
public interface UserSecondRechargeDayMapper extends BaseMapper<UserSecondRechargeDay> {
    /**
     * 获取当天开箱累计
     *
     * @param userId
     * @return
     */
    default UserSecondRechargeDay getTodayByUserId(Integer userId) {
        LambdaQueryWrapper<UserSecondRechargeDay> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserSecondRechargeDay::getUserId, userId);
        wrapper.eq(UserSecondRechargeDay::getFoundDate, DateUtils.toStringDate(new Date()));
        wrapper.orderByAsc(UserSecondRechargeDay::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    /**
     * 获取三日内开箱累计
     *
     * @param userId
     * @return
     */
    default List<UserSecondRechargeDay> findThreeDayByUserId(Integer userId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        LambdaQueryWrapper<UserSecondRechargeDay> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserSecondRechargeDay::getUserId, userId);
        wrapper.ge(UserSecondRechargeDay::getFoundDate, DateUtils.stringToDate(df.format(calendar.getTime())));
        return selectList(wrapper);
    }
}
