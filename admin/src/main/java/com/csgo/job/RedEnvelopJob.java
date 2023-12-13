package com.csgo.job;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.service.envelop.RedEnvelopService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class RedEnvelopJob {
    @Autowired
    private RedEnvelopService redEnvelopService;

//    @Scheduled(cron = "0 0 0 * * ?")
    public void autoDistributeRedEnvelop() {
        List<RedEnvelop> autoRedEnvelopList = redEnvelopService.find();
        redEnvelopService.expireThenDistribute(autoRedEnvelopList);
    }
}
