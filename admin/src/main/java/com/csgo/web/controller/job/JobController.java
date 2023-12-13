package com.csgo.web.controller.job;

import com.csgo.job.RedEnvelopJob;
import com.csgo.job.UserCommissionLogJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 */
@RestController
public class JobController {

    @Autowired
    private RedEnvelopJob redEnvelopJob;
    @Autowired
    private UserCommissionLogJob userCommissionLogJob;

    @GetMapping("/{type}")
    public void trigger(@PathVariable("type") JobType type) {
        switch (type) {
            case COMMISSION:
                userCommissionLogJob.doCommission();
                return;
            case RED_ENVELOP:
                redEnvelopJob.autoDistributeRedEnvelop();
                return;
            default:
        }
    }
}
