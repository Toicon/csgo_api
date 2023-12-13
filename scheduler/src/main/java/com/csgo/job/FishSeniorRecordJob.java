package com.csgo.job;

import com.csgo.domain.enums.FishSessionTypeEnum;
import com.csgo.domain.plus.fish.FishUserActivityHook;
import com.csgo.service.RedissonLockService;
import com.csgo.service.fish.FishJobService;
import com.echo.framework.scheduler.Job;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 钓鱼作业--高级场
 */
@Service
@Slf4j
public class FishSeniorRecordJob extends Job {

    @Autowired
    private FishJobService fishJobService;
    @Autowired
    private RedissonLockService redissonLockService;

    @Override
    protected void execute() throws Throwable {
        log.info("开始执行定时钓鱼作业--高级场");
        String lockKey = "LOCK:FISHJOB:" + FishSessionTypeEnum.SENIOR.getCode();
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 30, TimeUnit.SECONDS);
            if (rLock == null) {
                log.error("定时钓鱼作业已经在执行中");
            } else {
                List<FishUserActivityHook> fishUserActivityHookList = fishJobService.getActivityHookList(FishSessionTypeEnum.SENIOR.getCode());
                if (CollectionUtils.isEmpty(fishUserActivityHookList)) {
                    log.info("获取不到已到期的钓鱼明细");
                    return;
                }
                fishUserActivityHookList.forEach(fishUserActivity -> {
                    try {
                        fishJobService.timeFish(fishUserActivity);
                    } catch (Exception ex) {
                        log.error("活动id执行失败:{}", fishUserActivity.getActivityId());
                    }
                });
            }
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
        log.info("结束执行定时钓鱼作业--高级场");
    }
}
