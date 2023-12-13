package com.csgo.job;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.service.envelop.RedEnvelopService;
import com.echo.framework.scheduler.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class RedEnvelopJob extends Job {
    @Autowired
    private RedEnvelopService redEnvelopService;

    @Override
    protected void execute() throws Throwable {
        log.info("--------定时处理过期红包------------------");
        List<RedEnvelop> autoRedEnvelopList = redEnvelopService.find();
        redEnvelopService.expireThenDistribute(autoRedEnvelopList);
    }
}
