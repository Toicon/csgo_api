package com.csgo.job;

import com.csgo.service.data.CleanDataService;
import com.echo.framework.scheduler.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CleanInnerDataJob extends Job {

    @Autowired
    private CleanDataService cleanDataService;

    @Override
    protected void execute() throws Throwable {
        cleanDataService.cleanInnerData();
    }
}
