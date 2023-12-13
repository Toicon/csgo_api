package com.csgo.support;

import com.csgo.constants.SystemConfigConstants;
import com.csgo.constants.UserConstants;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.config.ExchangeRatePlus;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.membership.Membership;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.domain.plus.membership.MembershipLevelRecord;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.second.UserSecondRechargeCoupon;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.config.ExchangeRatePlusMapper;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import com.csgo.mapper.plus.membership.MembershipLevelConfigMapper;
import com.csgo.mapper.plus.membership.MembershipLevelRecordMapper;
import com.csgo.mapper.plus.membership.MembershipMapper;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.mapper.plus.second.UserSecondRechargeCouponMapper;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.pay.hook.UserRechargeHookManage;
import com.csgo.service.membership.MembershipTaskRecordService;
import com.csgo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author admin
 */
@Component
public class OrderRecordBuilder {

    @Autowired
    private UserRechargeHookManage userRechargeHookManage;
    @Autowired
    private ExchangeRatePlusMapper exchangeRatePlusMapper;
    @Autowired
    private OrderRecordMapper orderRecordMapper;
    @Autowired
    private UserBalancePlusMapper userBalancePlusMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private MembershipLevelConfigMapper membershipLevelConfigMapper;
    @Autowired
    private MembershipMapper membershipMapper;
    @Autowired
    private MembershipLevelRecordMapper membershipLevelRecordMapper;
    @Autowired
    private MembershipTaskRecordService membershipTaskRecordService;
    @Autowired
    private UserSecondRechargeCouponMapper userSecondRechargeCouponMapper;

    @Transactional
    public OrderRecord createOrderRecord(CreateOrderRecordContext context) {
        UserPlus user = context.getUser();
        BigDecimal discount = this.updateUserStateRemind(user.getId(), context.getMhtOrderNo());
        if (discount != null) {
            discount = context.getPrice().multiply(BigDecimal.ONE.subtract(discount)).setScale(2, BigDecimal.ROUND_DOWN);
            if (context.getExtraPrice() == null) {
                context.setExtraPrice(discount);
            } else {
                context.setExtraPrice(context.getExtraPrice().add(discount));
            }
        }
        OrderRecord orderRecord = to(context);
        if ((user.getFlag() == GlobalConstants.INTERNAL_USER_FLAG && context.isInnerRecharge()) || context.isForceRecharge()) {
            OrderRecord firstRecharge = orderRecordMapper.findFirst(user.getId());
            if (context.isFirst() && null == firstRecharge) {
                orderRecord.setExtraPrice(orderRecord.getExtraPrice().add(orderRecord.getOrderAmount().multiply(new BigDecimal(0.1)).setScale(2, RoundingMode.HALF_UP)));
                orderRecord.setFirst(true);
            }
            user.setBalance(user.getBalance().add(orderRecord.getOrderAmount().add(orderRecord.getExtraPrice())));
            List<OrderRecord> orderRecords = findRecharge(user.getId(), "2");
            AtomicReference<BigDecimal> rechargeAmount = new AtomicReference<>(orderRecord.getOrderAmount());
            orderRecords.forEach(record -> rechargeAmount.getAndSet(rechargeAmount.get().add(record.getOrderAmount())));
            //内部员工充值金额加用户余额，超过系统限定则报错
            SystemConfig systemConfig = systemConfigMapper.get(SystemConfigConstants.RECHARGE_LIMIT);
            if (null == systemConfig || !org.springframework.util.StringUtils.hasText(systemConfig.getValue())
                    || rechargeAmount.get().compareTo(new BigDecimal(systemConfig.getValue())) > -1) {
                throw new BusinessException(ExceptionCode.PAY_FAILURE);
            }
            orderRecord.setOrderStatus(GlobalConstants.ORDER_PAY_SUCCESS);
            orderRecord.setPaidTime(new Date());
            user.setPayMoney(user.getPayMoney().add(orderRecord.getPaidAmount()));
            cost(user, orderRecord.getOrderAmount().add(orderRecord.getExtraPrice()));
            userPlusMapper.updateById(user);
            userRechargeHookManage.handleUserRechargeSuccess(user, orderRecord);
        }
        if (context.getExistRecord() != null) {
            orderRecordMapper.updateById(orderRecord);
        } else {
            orderRecordMapper.insert(orderRecord);
        }
        return orderRecord;
    }

    public void membershipHandler(Membership membership, BigDecimal orderAmount, boolean isFirst) {
        if (isFirst) {
            orderAmount = orderAmount.multiply(BigDecimal.valueOf(2));
        }
        membership.setGrowth(membership.getGrowth().add(orderAmount));
        List<MembershipLevelConfig> configs = membershipLevelConfigMapper.maxLevel(membership.getGrowth());
        if (!CollectionUtils.isEmpty(configs)) {
            membership.setGrade(configs.get(0).getLevel());
        }
        membership.setUt(new Date());
        membershipMapper.updateById(membership);
        MembershipLevelRecord record = new MembershipLevelRecord();
        record.setUserId(membership.getUserId());
        record.setGrowth(orderAmount);
        record.setCurrentGrowth(membership.getGrowth());
        record.setCurrentGrade(membership.getGrade());
        record.setRemark("充值");
        record.setCt(new Date());
        membershipLevelRecordMapper.insert(record);
    }

    private OrderRecord to(CreateOrderRecordContext context) {
        UserPlus user = context.getUser();
        Membership membership = membershipMapper.get(user.getId());
//        BigDecimal totalGrowth = membership.getGrowth().add(context.getPrice());
//        List<MembershipLevelConfig> configs = membershipLevelConfigMapper.maxLevel(totalGrowth);
        context.setMembership(membership);
        if (context.getExistRecord() != null) {
            return context.getExistRecord();
        }
        BigDecimal price = context.getPrice();
        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setStyle(context.getStyle());
        orderRecord.setUserId(user.getId());
        if (user.getParentId() == null) {
            //默认挂未分类主播
            orderRecord.setParentId(UserConstants.UNCLASSIFIED_USER_ID);
        } else {
            orderRecord.setParentId(user.getParentId());
        }
        orderRecord.setCreateTime(new Date());
        orderRecord.setOrderNum(context.getMhtOrderNo());
        List<ExchangeRatePlus> exchangeRateList = exchangeRatePlusMapper.findByFlag(1);
        if (CollectionUtils.isEmpty(exchangeRateList)) {
            throw new BusinessException(ExceptionCode.RATE_NOT_EXISTS);
        }
        ExchangeRatePlus exchangeRate = exchangeRateList.get(0);
        String rate = exchangeRate.getExchangeRate();
        orderRecord.setPaidAmount(price.multiply(BigDecimal.valueOf(Double.parseDouble(rate))).setScale(0, RoundingMode.HALF_UP));
        orderRecord.setOrderAmount(price);
        orderRecord.setOrderStatus("0");
        orderRecord.setUserPhone(user.getPhone());
        orderRecord.setUserPhone(user.getUserName());
        orderRecord.setFirst(context.isFirst());
        BigDecimal payGiveMoney = exchangeRate.getPayGiveMoney();
        BigDecimal extraPrice = context.getExtraPrice();
        if (payGiveMoney != null) {
            extraPrice = extraPrice.add(price.multiply(payGiveMoney.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP)));
        }

//        if (!CollectionUtils.isEmpty(configs)) {
//            MembershipLevelConfig config = configs.get(0);
//            BigDecimal extra = orderRecord.getOrderAmount().multiply(config.getGiftRate()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
//            extraPrice = extraPrice.add(extra);
//        }
        orderRecord.setExtraPrice(extraPrice);
        orderRecord.setCreateTime(new Date());
        return orderRecord;
    }

    //充值余额记录
    public void cost(UserPlus user, BigDecimal price) {
        UserBalancePlus userBalance = new UserBalancePlus();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(price);
        userBalance.setType(1); //收入
        userBalance.setRemark("充值");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalancePlusMapper.insert(userBalance);
    }

    private List<OrderRecord> findRecharge(Integer userId, String orderStatus) {
        return findRecharge(userId, new Date(), null, orderStatus);
    }

    private List<OrderRecord> findRecharge(Integer userId, Date startDate, Date endDate, String orderStatus) {
        return orderRecordMapper.findLimitDate(userId, startDate, endDate, orderStatus);
    }

    /**
     * 修改续充优惠状态
     *
     * @param userId
     * @param orderNum
     * @return
     */
    private BigDecimal updateUserStateRemind(Integer userId, String orderNum) {
        UserSecondRechargeCoupon userSecondRechargeCoupon = userSecondRechargeCouponMapper.getEffectiveByUserId(userId);
        if (userSecondRechargeCoupon != null) {
            if (userSecondRechargeCoupon.getFailureTime().after(new Date())) {
                if (userSecondRechargeCoupon.getUseState().equals(YesOrNoEnum.NO.getCode())) {
                    userSecondRechargeCoupon.setUseState(YesOrNoEnum.YES.getCode());
                    userSecondRechargeCoupon.setOrderNum(orderNum);
                    userSecondRechargeCoupon.setUpdateDate(new Date());
                    userSecondRechargeCouponMapper.updateById(userSecondRechargeCoupon);
                    return userSecondRechargeCoupon.getDiscountRatio();
                }
            }
        }
        return null;
    }

}
