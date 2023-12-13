package com.csgo.job;

import org.springframework.beans.factory.annotation.Autowired;
import com.csgo.service.ZbtProductFiltersService;
import com.echo.framework.scheduler.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Slf4j
public class ProductCategoryJob extends Job {

    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;


    @Override
    protected void execute() throws Throwable {
        log.info("--------定时更新产品类目------------------");
        zbtProductFiltersService.updateRefresh();
    }
}
