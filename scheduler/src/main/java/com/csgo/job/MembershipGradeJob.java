package com.csgo.job;

import com.csgo.service.user.MembershipService;
import com.echo.framework.scheduler.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MembershipGradeJob extends Job {

    @Autowired
    private MembershipService membershipService;

    @Override
    protected void execute() throws Throwable {
        membershipService.process();
    }
}
