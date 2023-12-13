package com.csgo.mapper.plus.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.order.SearchOrderRecordCondition;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.order.OrderRecordDTO;
import com.csgo.domain.report.StatisticsDTO;
import com.csgo.support.GlobalConstants;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRecordMapper extends BaseMapper<OrderRecord> {

    default List<OrderRecord> findByUserIds(List<Integer> userIds) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.in(OrderRecord::getUserId, userIds);
        wrapper.eq(OrderRecord::getOrderStatus, GlobalConstants.ORDER_STATUS_SUCCESS);
        return selectList(wrapper);
    }

    default int getCountByUserId(Integer userId) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderRecord::getUserId, userId);
        wrapper.eq(OrderRecord::getOrderStatus, GlobalConstants.ORDER_STATUS_SUCCESS);
        return selectCount(wrapper);
    }

    default OrderRecord get(String orderNum) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderRecord::getOrderNum, orderNum);
        return selectOne(wrapper);
    }

    default Page<OrderRecord> pagination(SearchOrderRecordCondition condition) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        if (null != condition.getUserId()) {
            wrapper.eq(OrderRecord::getUserId, condition.getUserId());
        }
        if (null != condition.getStartDate()) {
            wrapper.ge(OrderRecord::getPaidTime, condition.getStartDate());
        }
        if (null != condition.getEndDate()) {
            wrapper.le(OrderRecord::getPaidTime, condition.getEndDate());
        }
        if (StringUtils.hasText(condition.getName())) {
            wrapper.like(OrderRecord::getUserPhone, condition.getName());
        }
        if (StringUtils.hasText(condition.getOrderNum())) {
            wrapper.like(OrderRecord::getOrderNum, condition.getOrderNum());
        }
        if (StringUtils.hasText(condition.getState())) {
            wrapper.eq(OrderRecord::getOrderStatus, condition.getState());
        }
        if (null != condition.getMinAmount()) {
            wrapper.ge(OrderRecord::getOrderAmount, condition.getMinAmount());
        }
        if (null != condition.getMaxAmount()) {
            wrapper.le(OrderRecord::getOrderAmount, condition.getMaxAmount());
        }
        if (StringUtils.hasText(condition.getFlag())) {
            if ("1".equals(condition.getFlag())) {
                wrapper.isNull(OrderRecord::getUpdateTime);
            } else {
                wrapper.isNotNull(OrderRecord::getUpdateTime);
            }
        }
        wrapper.orderByDesc(OrderRecord::getCreateTime);
        return selectPage(condition.getPage(), wrapper);
    }

    int getRechargeCount(@Param("userId") Integer userId);

    default List<OrderRecord> findLimitDate(Integer userId, Date startDate, Date endDate, String orderStatus) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        if (null != userId) {
            wrapper.eq(OrderRecord::getUserId, userId);
        }
        if (null != startDate) {
            wrapper.ge(OrderRecord::getPaidTime, DateUtils.truncate(startDate, Calendar.DATE));
        }
        if (null != endDate) {
            wrapper.le(OrderRecord::getPaidTime, DateUtils.truncate(endDate, Calendar.DATE));
        }
        if (StringUtils.hasText(orderStatus)) {
            wrapper.eq(OrderRecord::getOrderStatus, orderStatus);
        }
        return selectList(wrapper);
    }

    default List<OrderRecord> findRedLimitDate(Integer userId, Date startDate, Date endDate, String orderStatus) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        if (null != userId) {
            wrapper.eq(OrderRecord::getUserId, userId);
        }
        if (null != startDate) {
            wrapper.ge(OrderRecord::getPaidTime, startDate);
        }
        if (null != endDate) {
            wrapper.lt(OrderRecord::getPaidTime, endDate);
        }
        if (StringUtils.hasText(orderStatus)) {
            wrapper.eq(OrderRecord::getOrderStatus, orderStatus);
        }
        return selectList(wrapper);
    }

    default List<OrderRecord> find(Integer userId, Date startDate, Date endDate, String orderStatus) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        if (null != userId) {
            wrapper.eq(OrderRecord::getUserId, userId);
        }
        if (null != startDate) {
            wrapper.ge(OrderRecord::getPaidTime, startDate);
        }
        if (null != endDate) {
            wrapper.le(OrderRecord::getPaidTime, endDate);
        }
        if (StringUtils.hasText(orderStatus)) {
            wrapper.eq(OrderRecord::getOrderStatus, orderStatus);
        }
        return selectList(wrapper);
    }

    BigDecimal anchorCharge(@Param("userId") Integer userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 当日充值金额(报表)
     *
     * @return
     */
    List<StatisticsDTO> dailyStatisticalReport(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("dataScope") String dataScope);

    /**
     * 当日充值金额
     *
     * @return
     */
    List<StatisticsDTO> dailyStatistical(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 当日活跃付费用户
     *
     * @return
     */
    List<StatisticsDTO> countActive(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("dataScope") String dataScope);

    List<OrderRecordDTO> findRecharge(@Param("status") String status, @Param("name") String name, @Param("flag") String flag, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    int countRechargeTotal(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    default OrderRecord findFirst(Integer userId) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderRecord::getUserId, userId);
        wrapper.eq(OrderRecord::getOrderStatus, GlobalConstants.ORDER_STATUS_SUCCESS);
        wrapper.eq(OrderRecord::isFirst, true);
        return selectOne(wrapper);
    }

    /**
     * 获取用户累计充值金额
     *
     * @param userId
     * @return
     */
    default BigDecimal getSumUserPaySuccess(Integer userId) {
        BigDecimal sumPrice = BigDecimal.ZERO;
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderRecord::getUserId, userId);
        wrapper.eq(OrderRecord::getOrderStatus, GlobalConstants.ORDER_STATUS_SUCCESS);
        List<OrderRecord> list = selectList(wrapper);
        if (list != null && list.size() > 0) {
            for (OrderRecord item : list) {
                sumPrice = sumPrice.add(item.getOrderAmount());
            }
        }
        return sumPrice;
    }

    /**
     * 修改用户未挂推广码并且成功支付订单
     *
     * @param userId
     * @param parentId
     */
    default void updateParentIdByUserId(Integer userId, Integer parentId) {
        LambdaUpdateWrapper<OrderRecord> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(OrderRecord::getUserId, userId);
        wrapper.eq(OrderRecord::getParentId, 0);
        wrapper.eq(OrderRecord::getOrderStatus, GlobalConstants.ORDER_STATUS_SUCCESS);
        wrapper.set(OrderRecord::getParentId, parentId);
        this.update(null, wrapper);
    }

    BigDecimal getSumSuccessOrderAmount(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
