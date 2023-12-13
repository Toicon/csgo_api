package com.csgo.service.second;

import com.csgo.domain.plus.second.UserSecondRechargeDay;
import com.csgo.mapper.plus.second.UserSecondRechargeDayMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 用户每日开箱累计
 */
@Slf4j
@Service
public class UserSecondRechargeDayService {


    @Autowired
    private UserSecondRechargeDayMapper userSecondRechargeDayMapper;

    @Transactional
    public void save(Integer userId, Integer openCount, BigDecimal consumeAmount) {
        //新增或者修改用户每日开箱累计
        UserSecondRechargeDay userSecondRechargeDay = userSecondRechargeDayMapper.getTodayByUserId(userId);
        if (userSecondRechargeDay != null) {
            userSecondRechargeDay.setOpenCount(userSecondRechargeDay.getOpenCount() + openCount);
            userSecondRechargeDay.setConsumeAmount(userSecondRechargeDay.getConsumeAmount().add(consumeAmount));
            userSecondRechargeDay.setUpdateDate(new Date());
            userSecondRechargeDayMapper.updateById(userSecondRechargeDay);
        } else {
            userSecondRechargeDay = new UserSecondRechargeDay();
            userSecondRechargeDay.setUserId(userId);
            userSecondRechargeDay.setOpenCount(Long.valueOf(openCount));
            userSecondRechargeDay.setConsumeAmount(consumeAmount);
            userSecondRechargeDay.setCreateDate(new Date());
            userSecondRechargeDay.setFoundDate(new Date());
            userSecondRechargeDayMapper.insert(userSecondRechargeDay);
        }
    }

}
