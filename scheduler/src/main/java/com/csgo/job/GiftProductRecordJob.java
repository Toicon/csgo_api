package com.csgo.job;

import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.service.product.GiftProductRecordJobService;
import com.echo.framework.scheduler.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author admin
 */
@Service
@Slf4j
public class GiftProductRecordJob extends Job {

    @Autowired
    private GiftProductRecordJobService giftProductRecordService;

    @Override
    protected void execute() throws Throwable {
        List<GiftPlus> giftList = giftProductRecordService.findProbabilityGiftList();
        if (CollectionUtils.isEmpty(giftList)) {
            log.info("获取价格权重礼包列表为空");
            return;
        }
        log.info("开始执行价格权重礼包物品更新");
        giftList.forEach(gift -> {
            giftProductRecordService.updateGiftProductByPrice(gift.getId());
        });
        log.info("结束执行价格权重礼包物品更新");
    }
}
