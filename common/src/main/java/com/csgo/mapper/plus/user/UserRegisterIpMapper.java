package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.user.UserRegisterIp;
import com.csgo.util.DateUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author admin
 */
@Repository
public interface UserRegisterIpMapper extends BaseMapper<UserRegisterIp> {
    /**
     * ip获取每日注册记录
     *
     * @param ip
     * @return
     */
    default UserRegisterIp getByIp(String ip) {
        LambdaQueryWrapper<UserRegisterIp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRegisterIp::getIp, ip);
        wrapper.eq(UserRegisterIp::getRegDate, DateUtils.stringToDate(DateUtils.toStringDate(new Date())));
        return selectOne(wrapper);
    }
}
