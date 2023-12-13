package com.csgo.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.csgo.service.user.UserCommissionLogService;
import com.echo.framework.scheduler.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class UserCommissionLogJob extends Job {

    @Autowired
    private UserCommissionLogService userCommissionLogService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void doCommission() {

    }

    @Override
    protected void execute() throws Throwable {
        log.info("--------定时执行用户分佣状态检查------------------");
        userCommissionLogService.refreshStatus();
    }
}
