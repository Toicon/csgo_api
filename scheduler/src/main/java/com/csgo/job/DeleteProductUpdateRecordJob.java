package com.csgo.job;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.service.product.ProductUpdateRecordService;
import com.csgo.util.DateUtilsEx;
import com.echo.framework.scheduler.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class DeleteProductUpdateRecordJob extends Job {
    @Autowired
    private ProductUpdateRecordService productUpdateRecordService;

    @Override
    protected void execute() throws Throwable {
        log.info("--------定时删除今天以前的商品价格修改错误记录------------------");
        Date date = new Date();
        productUpdateRecordService.delete(DateUtilsEx.toDayStart(date));
    }
}
