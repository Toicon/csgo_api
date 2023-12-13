package com.csgo.service.second;

import com.csgo.constants.SystemConfigConstants;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.second.UserSecondRechargeCoupon;
import com.csgo.domain.plus.second.UserSecondRechargeDay;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.exception.ServiceErrorException;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.mapper.plus.recharge.RechargeChannelPriceItemMapper;
import com.csgo.mapper.plus.second.UserSecondRechargeCouponMapper;
import com.csgo.mapper.plus.second.UserSecondRechargeDayMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.service.lock.RedissonLockService;
import com.csgo.util.DateUtilsEx;
import com.csgo.util.LotteryUtil;
import com.csgo.web.response.second.UserSecondRechargeRemindResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 用户二次充值提醒
 */
@Slf4j
@Service
public class UserSecondRechargeRemindService {


    @Autowired
    private UserSecondRechargeDayMapper userSecondRechargeDayMapper;
    @Autowired
    private UserSecondRechargeCouponMapper userSecondRechargeCouponMapper;
    @Autowired
    private OrderRecordMapper orderRecordMapper;
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private RechargeChannelPriceItemMapper rechargeChannelPriceItemMapper;

    @Autowired
    private RedissonLockService redissonLockService;

    /**
     * 倒计时
     *
     * @param nextDate
     * @return
     */
    private Integer getCountDown(Date nextDate) {
        Date now = new Date();
        int countDown = 0;
        if (nextDate != null) {
            countDown = DateUtilsEx.getDifferSecond(now, nextDate).intValue();
        }
        return countDown;
    }

    @Transactional
    public UserSecondRechargeRemindResponse getSecondRechargeRemind(Integer userId) {
        Integer countDown = null;
        BigDecimal discount = null;
        boolean receiveState = false;
        UserSecondRechargeRemindResponse response = new UserSecondRechargeRemindResponse();
        String lockKey = "second:recharge:" + userId;
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new ServiceErrorException("不允许重复调用");
            }
            UserSecondRechargeCoupon userSecondRechargeCoupon = userSecondRechargeCouponMapper.getEffectiveByUserId(userId);
            if (userSecondRechargeCoupon == null) {
                //判断是否是大于等于1次充值
                int payCount = orderRecordMapper.getCountByUserId(userId);
                if (payCount >= 1) {
                    //触发概率
                    BigDecimal probability;
                    SystemConfig probabilityConfig = systemConfigMapper.get(SystemConfigConstants.SYSTEM_SECOND_PROBABILITY);
                    if (probabilityConfig == null) {
                        throw new ServiceErrorException("触发概率配置不存在");
                    }
                    try {
                        probability = BigDecimal.valueOf(Double.valueOf(probabilityConfig.getValue()));
                    } catch (Exception ex) {
                        throw new ServiceErrorException("触发概率配置不正确");
                    }
                    if (probability.compareTo(BigDecimal.ONE) > 0) {
                        throw new ServiceErrorException("触发概率不能大于1");
                    }
                    //判断触发概率
                    List<Double> probabilityRatio = new ArrayList<>();
                    probabilityRatio.add(probability.doubleValue());
                    probabilityRatio.add(BigDecimal.ONE.subtract(probability).doubleValue());
                    int probabilityNum = LotteryUtil.draw(probabilityRatio);
                    if (probabilityNum != 0) {
                        //没有触发，返回结果
                        response.setReceiveState(false);
                        return response;
                    }
                    //续充倒计时
                    Integer countDownCfg;
                    //开箱均价涨幅
                    BigDecimal increase;
                    //续充折扣起始
                    BigDecimal minDiscount;
                    //续充折扣截止
                    BigDecimal maxDiscount;

                    SystemConfig countDownConfig = systemConfigMapper.get(SystemConfigConstants.SYSTEM_SECOND_COUNTDOWN);
                    if (countDownConfig == null) {
                        throw new ServiceErrorException("续充倒计时配置不存在");
                    }
                    try {
                        countDownCfg = Integer.valueOf(countDownConfig.getValue());
                    } catch (Exception ex) {
                        throw new ServiceErrorException("续充倒计时配置不正确");
                    }
                    //开箱均价涨幅
                    SystemConfig increaseConfig = systemConfigMapper.get(SystemConfigConstants.SYSTEM_CUSTOMER_PRICE);
                    if (increaseConfig == null) {
                        throw new ServiceErrorException("开箱均价涨幅配置不存在");
                    }
                    try {
                        increase = BigDecimal.valueOf(Double.valueOf(increaseConfig.getValue()));
                    } catch (Exception ex) {
                        throw new ServiceErrorException("开箱均价涨幅配置不正确");
                    }
                    SystemConfig minDiscountConfig = systemConfigMapper.get(SystemConfigConstants.SYSTEM_DISCOUNT_MIN);
                    if (minDiscountConfig == null) {
                        throw new ServiceErrorException("续充折扣起始配置不存在");
                    }
                    try {
                        minDiscount = BigDecimal.valueOf(Double.valueOf(minDiscountConfig.getValue())).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN);
                    } catch (Exception ex) {
                        throw new ServiceErrorException("续充折扣起始配置不正确");
                    }
                    SystemConfig maxDiscountConfig = systemConfigMapper.get(SystemConfigConstants.SYSTEM_DISCOUNT_MAX);
                    if (maxDiscountConfig == null) {
                        throw new ServiceErrorException("续充折扣截止配置不存在");
                    }
                    try {
                        maxDiscount = BigDecimal.valueOf(Double.valueOf(maxDiscountConfig.getValue())).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN);
                    } catch (Exception ex) {
                        throw new ServiceErrorException("续充折扣截止配置不正确");
                    }
                    if (minDiscount.compareTo(maxDiscount) > 0) {
                        throw new ServiceErrorException("续充折扣起始不能大于续充折扣截止");
                    }
                    //获取三日内开箱累计
                    List<UserSecondRechargeDay> userSecondRechargeDayList = userSecondRechargeDayMapper.findThreeDayByUserId(userId);
                    if (CollectionUtils.isEmpty(userSecondRechargeDayList)) {
                        //没有开箱累计
                        response.setReceiveState(false);
                        return response;
                    }
                    //开箱次数
                    Long openCount = userSecondRechargeDayList.stream().map(UserSecondRechargeDay::getOpenCount).reduce(Long::sum).get();
                    //累计消耗
                    BigDecimal consumeAmount = userSecondRechargeDayList.stream().map(UserSecondRechargeDay::getConsumeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    //判断是否满足客单价
                    UserPlus userPlus = userPlusMapper.selectById(userId);
                    //客单价均值*客单价涨幅
                    BigDecimal customerRatio = BigDecimal.valueOf(openCount).multiply(increase).multiply(BigDecimal.valueOf(0.01));
                    BigDecimal avgCustomerPrice = consumeAmount.divide(customerRatio, 2, BigDecimal.ROUND_DOWN);
                    if (userPlus.getBalance().compareTo(avgCustomerPrice) < 0) {
                        receiveState = true;
                        countDown = countDownCfg;
                        discount = this.makeRandom(minDiscount.floatValue(), maxDiscount.floatValue(), 2);
                        UserSecondRechargeCoupon coupon = new UserSecondRechargeCoupon();
                        coupon.setUserId(userId);
                        coupon.setDiscountRatio(discount);
                        coupon.setUseState(YesOrNoEnum.NO.getCode());
                        coupon.setFailureTime(DateUtilsEx.calDateBySecond(new Date(), countDown));
                        coupon.setCreateDate(new Date());
                        userSecondRechargeCouponMapper.insert(coupon);
                    }
                }
            } else {
                //判断优惠券是否被使用
                if (userSecondRechargeCoupon.getUseState().equals(YesOrNoEnum.NO.getCode())) {
                    if (userSecondRechargeCoupon.getFailureTime().after(new Date())) {
                        discount = userSecondRechargeCoupon.getDiscountRatio();
                        countDown = this.getCountDown(userSecondRechargeCoupon.getFailureTime());
                        receiveState = true;
                    }
                }
            }
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
        response.setCountDown(countDown);
        response.setReceiveState(receiveState);
        if (discount != null) {
            response.setDiscount(BigDecimal.ONE.subtract(discount).multiply((BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_DOWN)));
        }
        return response;
    }


    /**
     * 返回二次充值折扣金额
     *
     * @param userId
     * @return
     */
    public BigDecimal getRechargeRemind(Integer userId, int priceItemId) {
        BigDecimal discount = null;
        RechargeChannelPriceItem rechargeChannelPriceItem = rechargeChannelPriceItemMapper.selectById(priceItemId);
        if (rechargeChannelPriceItem == null) {
            throw new ServiceErrorException("充值面额有误");
        }
        UserSecondRechargeCoupon userSecondRechargeCoupon = userSecondRechargeCouponMapper.getEffectiveByUserId(userId);
        if (userSecondRechargeCoupon != null) {
            if (userSecondRechargeCoupon.getFailureTime().after(new Date())) {
                //判断优惠券是否使用
                if (userSecondRechargeCoupon.getUseState().equals(YesOrNoEnum.NO.getCode())) {
                    discount = rechargeChannelPriceItem.getPrice().multiply(BigDecimal.ONE.subtract(userSecondRechargeCoupon.getDiscountRatio())).setScale(2, BigDecimal.ROUND_DOWN);
                }
            }
        }
        return discount;
    }

    /**
     * 生成指定范围，指定小数位数的随机数
     *
     * @param min   最小值
     * @param max   最大值
     * @param scale 小数位数
     * @return
     */
    BigDecimal makeRandom(float min, float max, int scale) {
        BigDecimal cha = new BigDecimal(Math.random() * (max - min) + min);
        return cha.setScale(scale, BigDecimal.ROUND_DOWN);//保留 scale 位小数，舍掉后面小数
    }

}
