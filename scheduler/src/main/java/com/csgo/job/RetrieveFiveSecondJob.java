package com.csgo.job;

import com.csgo.domain.enums.WithdrawDeliveryMethod;
import com.csgo.domain.enums.WithdrawPropItemStatus;
import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.mapper.plus.withdraw.WithdrawPropRelateMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.WithdrawOrderService;
import com.echo.framework.scheduler.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 2021/4/30
 */
@Service
@Slf4j
public class RetrieveFiveSecondJob extends Job {

    @Autowired
    private WithdrawPropRelateMapper withdrawPropRelateMapper;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private WithdrawOrderService withdrawOrderService;

    private static final String GLOBAL_WITHDRAW_ORDER_STATUS_SYNC_LOCK_KEY = "lock:global:withdraw_order_status_sync";

    @Override
    protected void execute() throws Throwable {
        log.info(">>>>> 执行发货状态同步开始------------------");
        if (StringUtils.hasText(redisTemplateFacde.get(GLOBAL_WITHDRAW_ORDER_STATUS_SYNC_LOCK_KEY))) {
            log.warn("[发货订单状态同步] 任务执行中 忽略此次调度");
            return;
        }
        redisTemplateFacde.set(GLOBAL_WITHDRAW_ORDER_STATUS_SYNC_LOCK_KEY, String.valueOf(System.currentTimeMillis()), 3, TimeUnit.MINUTES);
        try {
            doExecute();
        } finally {
            redisTemplateFacde.delete(GLOBAL_WITHDRAW_ORDER_STATUS_SYNC_LOCK_KEY);
        }
        log.info("<<<<< 执行发货状态同步结束------------------");
    }

    private void doExecute() {
        List<WithdrawPropRelate> relates = withdrawPropRelateMapper.findByStatus(
                Arrays.asList(
                        WithdrawPropItemStatus.PASS,
                        WithdrawPropItemStatus.AUTO,
                        WithdrawPropItemStatus.RETRIEVE,
                        WithdrawPropItemStatus.WAITING
                )
        );

        if (CollectionUtils.isEmpty(relates)) {
            log.info("[发货订单状态同步] relates is empty");
            return;
        }
        relates.forEach(relate -> {
            try {
                if (relate.getDeliveryMethod() == null) {
                    log.error("[发货订单状态同步] relate id:{} deliveryMethod is null", relate.getId());
                    return;
                }

                if (relate.getDeliveryMethod() == WithdrawDeliveryMethod.YY) {
                    // nothing
                } else if (relate.getDeliveryMethod() == WithdrawDeliveryMethod.IG) {
                    withdrawOrderService.syncStatusByIg(relate);
                } else if (relate.getDeliveryMethod() == WithdrawDeliveryMethod.ZBT) {
                    withdrawOrderService.syncStatusByZbt(relate);
                } else {
                    log.error("[发货订单状态同步] relate id:{} deliveryMethod is allow", relate.getId());
                }
            } catch (Exception e) {
                log.error("[发货订单状态同步] error:" + e.getMessage(), e);
            }
        });
    }

}
