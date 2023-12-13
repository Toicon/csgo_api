package com.csgo.config;

import com.csgo.job.*;
import com.echo.framework.scheduler.SchedulerImpl;
import com.echo.framework.scheduler.autoconfigure.JobRegistration;
import com.echo.framework.scheduler.info.JobStatistic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author admin
 * @since 2020/6/13
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {


    @Bean
    public JobRegistration jobRegistration(SchedulerImpl scheduler, JobStatistic jobStatistic) {
        return new JobRegistration(scheduler, jobStatistic)
                .triggerByCronExpression(UpdateBlindRoomJob.class.getSimpleName(), UpdateBlindRoomJob.class, "0/5 * * * * ?", "定时查看对应的房间状态是否可以结束")
                //.triggerByCronExpression(ProductCategoryJob.class.getSimpleName(), ProductCategoryJob.class, "0 0 3 * * ?", "每天3天更新zbt类目")
                .triggerByCronExpression(CleanInnerDataJob.class.getSimpleName(), CleanInnerDataJob.class, "0 0 6 * * ?", "每天6点清理内部用户数据")
                .triggerByCronExpression(RedEnvelopJob.class.getSimpleName(), RedEnvelopJob.class, "0 0 0 * * ?", "定时处理过期红包")
                .triggerByCronExpression(RetrieveFiveSecondJob.class.getSimpleName(), RetrieveFiveSecondJob.class, "0/15 * * * * ?", "每15秒查询报价情况")
                .triggerByCronExpression(RollJob.class.getSimpleName(), RollJob.class, "*/10 * * * * ?", "每10秒查看对应的房间开奖信息")
                .triggerByCronExpression(UserCommissionLogJob.class.getSimpleName(), UserCommissionLogJob.class, "0 0 0 * * ?", "定时执行用户分佣状态检查")
                .triggerByCronExpression(DeleteProductUpdateRecordJob.class.getSimpleName(), DeleteProductUpdateRecordJob.class, "0 0 1 * * ?", "定时删除今天以前的商品价格修改错误记录")
                .triggerByCronExpression(MembershipGradeJob.class.getSimpleName(), MembershipGradeJob.class, "0 0 0 1 * ?", "每月1号零点衰减VIP经验")
                .triggerByCronExpression(DeleteInnerRechargeJob.class.getSimpleName(), DeleteInnerRechargeJob.class, "0/5 * * * * ?", "每5秒删除超时的测试充值用户")
                .triggerByCronExpression(GiftProductRecordJob.class.getSimpleName(), GiftProductRecordJob.class, "0 0 0/3 * * ?", "每3小时价格权重礼包物品更新");

    }
}
