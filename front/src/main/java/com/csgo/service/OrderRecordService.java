package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.order.SearchOrderRecordCondition;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2021/4/30
 */
@Service
public class OrderRecordService {
    @Autowired
    private OrderRecordMapper mapper;

    public List<OrderRecord> findRecharge(Integer userId, String orderStatus) {
        return findRecharge(userId, new Date(), null, orderStatus);
    }

    public List<OrderRecord> findRecharge(Integer userId, Date startDate, Date endDate, String orderStatus) {
        return mapper.findLimitDate(userId, startDate, endDate, orderStatus);
    }

    public List<OrderRecord> findRedRecharge(Integer userId, Date startDate, Date endDate, String orderStatus) {
        return mapper.findRedLimitDate(userId, startDate, endDate, orderStatus);
    }

    public List<OrderRecord> findRollLimitRecharge(Integer userId, Date startDate, Date endDate, String orderStatus) {
        return mapper.find(userId, startDate, endDate, orderStatus);
    }

    @Transactional
    public void update(OrderRecord record) {
        record.setUpdateTime(new Date());
        mapper.updateById(record);
    }

    public List<OrderRecord> findByUserIds(int userId) {
        return mapper.findByUserIds(Collections.singletonList(userId));
    }

    public OrderRecord queryOrderNum(String orderNum) {
        return mapper.get(orderNum);
    }

    public Page<OrderRecord> pagination(SearchOrderRecordCondition condition) {
        return mapper.pagination(condition);
    }

    public OrderRecord findFirst(Integer userId) {
        return mapper.findFirst(userId);
    }

    public BigDecimal getSumUserPaySuccess(Integer userId) {
        return mapper.getSumUserPaySuccess(userId);
    }
}
