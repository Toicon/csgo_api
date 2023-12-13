package com.csgo.service.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.order.SearchOrderRecordCondition;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.order.OrderRecordDTO;
import com.csgo.domain.report.StatisticsDTO;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2021/4/30
 */
@Service
public class OrderRecordService {
    @Autowired
    private OrderRecordMapper mapper;

    @Autowired
    private AdminUserService adminUserService;

    public Page<OrderRecord> pagination(SearchOrderRecordCondition condition) {
        return mapper.pagination(condition);
    }

    public List<OrderRecord> findByUserIds(List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return mapper.findByUserIds(userIds);
    }

    public List<OrderRecordDTO> find(String name, String flag, Date startDate, Date endDate) {
        return mapper.findRecharge("2", name, flag, startDate, endDate);
    }

    /**
     * 当日充值金额
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<StatisticsDTO> dailyStatistical(String startDate, String endDate) {
        String dataScope = adminUserService.getUserDataScope("adminUser");
        return mapper.dailyStatisticalReport(startDate, endDate, dataScope);
    }

    /**
     * 日活跃付费用户
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<StatisticsDTO> countActive(String startDate, String endDate) {
        String dataScope = adminUserService.getUserDataScope("adminUser");
        return mapper.countActive(startDate, endDate, dataScope);
    }

    public int countRechargeTotal(Date startDate, Date endDate) {
        return mapper.countRechargeTotal(startDate, endDate);
    }

    public List<StatisticsDTO> dailyStatistical(Date startDate, Date endDate) {
        return mapper.dailyStatistical(startDate, endDate);
    }
}
