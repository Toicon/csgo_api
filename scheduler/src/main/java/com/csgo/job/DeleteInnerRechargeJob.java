package com.csgo.job;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.user.UserInnerRechargeLimit;
import com.csgo.service.user.UserBalanceService;
import com.echo.framework.scheduler.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class DeleteInnerRechargeJob extends Job {
    @Autowired
    private UserBalanceService userBalanceService;

    @Override
    protected void execute() throws Throwable {
        log.info("--------定时清除用户测试充值属性------------------");
        Date date = new Date();
        List<UserInnerRechargeLimit> rechargeLimits = userBalanceService.findNeedRemoveInnerRecharge(date);
        for (UserInnerRechargeLimit rechargeLimit : rechargeLimits) {
            try {
                userBalanceService.removeInnerRecharge(rechargeLimit);
            } catch (Exception e) {
                log.error("清理用户测试充值属性出错，msg：{}", e);
            }
        }
    }
}
