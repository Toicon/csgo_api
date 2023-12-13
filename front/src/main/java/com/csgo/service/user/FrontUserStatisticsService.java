package com.csgo.service.user;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.gift.GiftType;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserLuckyHistoryLowProbabilityDTO;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.mo.RecentDateMO;
import com.csgo.mapper.UserCommissionLogMapper;
import com.csgo.mapper.UserLuckyHistoryMapper;
import com.csgo.mapper.plus.code.ActivationCodeMapper;
import com.csgo.mapper.plus.envelop.RedEnvelopRecordMapper;
import com.csgo.mapper.plus.gift.GiftTypeMapper;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.mapper.plus.user.UserCommissionLogPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.BackpackFromSourceConstant;
import com.csgo.modular.product.model.front.ProductSimpleVO;
import com.csgo.web.controller.user.model.*;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FrontUserStatisticsService {

    private final UserPlusMapper userPlusMapper;

    private final UserMessagePlusMapper userMessagePlusMapper;

    private final UserLuckyHistoryMapper userLuckyHistoryMapper;

    private final OrderRecordMapper orderRecordMapper;

    private final RedEnvelopRecordMapper redEnvelopRecordMapper;

    private final ActivationCodeMapper activationCodeMapper;

    private final UserCommissionLogPlusMapper userCommissionLogPlusMapper;
    private final UserCommissionLogMapper userCommissionLogMapper;

    private final GiftTypeMapper giftTypeMapper;

    private List<String> getTypeList() {
        List<GiftType> giftTypes = giftTypeMapper.selectList(null);
        return giftTypes.stream().map(GiftType::getType).collect(Collectors.toList());
    }

    public UserPersonalVO getUser(Integer userId) {
        UserPlus user = userPlusMapper.selectById(userId);
        if (user == null) {
            throw BizServerException.of(CommonBizCode.USER_NOT_FOUND);
        }
        RecentDateMO recentDateMO = new RecentDateMO();

        UserPersonalVO vo = new UserPersonalVO();

        // 用户信息
        Integer onlineDayCount = getOnlineDayCount(user.getCreatedAt());

        vo.setUserId(userId);
        vo.setUserName(user.getName());
        vo.setUserImg(user.getImg());

        vo.setOnlineDayCount(onlineDayCount);

        List<String> typeList = getTypeList();

        // 开箱金额
        BigDecimal openBoxRewardMoneyToday = userMessagePlusMapper.getSumOpenBoxMoney(userId, recentDateMO.getToday().getStart().toDate(), typeList);
        vo.setOpenBoxRewardMoneyToday(openBoxRewardMoneyToday);

        // 幸运饰品
        UserLuckyHistoryLowProbabilityDTO last7DayLowProbabilityProduct = getLast7DayLowProbabilityProduct(userId);
        vo.setLowProbabilityProduct(last7DayLowProbabilityProduct);
        return vo;
    }

    private Integer getOnlineDayCount(Date registerDate) {
        if (registerDate == null) {
            return 0;
        }
        DateTime dateTime = new DateTime(registerDate);

        return Days.daysBetween(dateTime, DateTime.now()).getDays();
    }

    private UserLuckyHistoryLowProbabilityDTO getLast7DayLowProbabilityProduct(Integer userId) {
        DateTime startDate = DateTime.now().withTimeAtStartOfDay().minusDays(6);
        UserLuckyHistoryLowProbabilityDTO lucky = userLuckyHistoryMapper.getLowProbabilityLuckyProduct(userId, startDate.toDate());
        UserLuckyHistoryLowProbabilityDTO unLucky = userLuckyHistoryMapper.getLowProbabilityUnLuckyProduct(userId, startDate.toDate());

        List<UserLuckyHistoryLowProbabilityDTO> list = Lists.newArrayList();
        if (lucky != null) {
            list.add(lucky);
        }
        if (unLucky != null) {
            list.add(unLucky);
        }

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        list.sort(Comparator.comparing(UserLuckyHistoryLowProbabilityDTO::getProbability)
                .thenComparing(UserLuckyHistoryLowProbabilityDTO::getProductPrice, Comparator.reverseOrder()));

        return list.get(0);
    }

    public UserPersonalRechargeVO getRecharge(Integer userId) {
        RecentDateMO recentDateMO = new RecentDateMO();

        // 充值统计
        BigDecimal rechargeMoneyToday = orderRecordMapper.getSumSuccessOrderAmount(userId, recentDateMO.getToday().getStart().toDate(), null);
        BigDecimal rechargeMoneyWeek = orderRecordMapper.getSumSuccessOrderAmount(userId, recentDateMO.getThisWeek().getStart().toDate(), null);
        BigDecimal rechargeMoneyMonth = orderRecordMapper.getSumSuccessOrderAmount(userId, recentDateMO.getThisMonth().getStart().toDate(), null);

        UserPersonalRechargeVO vo = new UserPersonalRechargeVO();
        vo.setUserId(userId);
        vo.setRechargeMoneyToday(rechargeMoneyToday);
        vo.setRechargeMoneyWeek(rechargeMoneyWeek);
        vo.setRechargeMoneyMonth(rechargeMoneyMonth);

        return vo;
    }

    public UserPersonalOpenBoxVO getOpenBox(Integer userId) {
        RecentDateMO recentDateMO = new RecentDateMO();

        // 开箱统计
        Integer openBoxCountToday = userMessagePlusMapper.getCountBySource(userId, recentDateMO.getToday().getStart().toDate(), BackpackFromSourceConstant.BOX);
        Integer openBoxCountWeek = userMessagePlusMapper.getCountBySource(userId, recentDateMO.getThisWeek().getStart().toDate(), BackpackFromSourceConstant.BOX);
        Integer openBoxCountMonth = userMessagePlusMapper.getCountBySource(userId, recentDateMO.getThisMonth().getStart().toDate(), BackpackFromSourceConstant.BOX);

        UserPersonalOpenBoxVO vo = new UserPersonalOpenBoxVO();
        vo.setUserId(userId);
        vo.setOpenBoxCountToday(openBoxCountToday);
        vo.setOpenBoxCountWeek(openBoxCountWeek);
        vo.setOpenBoxCountMonth(openBoxCountMonth);

        return vo;
    }

    public UserPersonalScoreVO getScore(Integer userId) {
        // 奖励
        BigDecimal redEnvelopReceiveMoney = redEnvelopRecordMapper.getSumAmount(userId);
        BigDecimal cdkReceiveMoney = activationCodeMapper.getSumPrice(userId);
        // UserCommissionLog recommendCommission = userCommissionLogMapper.getRecommendCommission(userId);
        // BigDecimal inviteUserRewardMoney = userCommissionLogPlusMapper.getReceiveAmount(userId);

        UserPersonalScoreVO vo = new UserPersonalScoreVO();
        vo.setUserId(userId);
        vo.setRedEnvelopReceiveMoney(redEnvelopReceiveMoney);
        vo.setCdkReceiveMoney(cdkReceiveMoney);
        return vo;
    }

    public UserPersonalPlayWayVO getPlayWay(Integer userId) {
        RecentDateMO recentDateMO = new RecentDateMO();

        // List<String> typeList = getTypeList();

        // 饰品
        UserPersonalPlayWayVO.PlayWay allWay = getMaxPriceProduct(userId, recentDateMO, null);
        UserPersonalPlayWayVO.PlayWay mineWay = getMaxPriceProduct(userId, recentDateMO, BackpackFromSourceConstant.MINE);
        UserPersonalPlayWayVO.PlayWay luckyBoxWay = getMaxPriceProduct(userId, recentDateMO, BackpackFromSourceConstant.LUCKY);
        UserPersonalPlayWayVO.PlayWay boxWay = getMaxPriceProduct(userId, recentDateMO, BackpackFromSourceConstant.BOX);

        UserPersonalPlayWayVO vo = new UserPersonalPlayWayVO();
        vo.setUserId(userId);
        vo.setAllWay(allWay);
        vo.setMineWay(mineWay);
        vo.setLuckyBoxWay(luckyBoxWay);
        vo.setBoxWay(boxWay);

        return vo;
    }

    private UserPersonalPlayWayVO.PlayWay getMaxPriceProduct(Integer userId, RecentDateMO mo, Integer fromSource) {
        ProductSimpleVO today = userMessagePlusMapper.getMaxPriceProductBySource(userId, mo.getToday().getStart().toDate(), fromSource);
        ProductSimpleVO week = userMessagePlusMapper.getMaxPriceProductBySource(userId, mo.getThisWeek().getStart().toDate(), fromSource);
        ProductSimpleVO month = userMessagePlusMapper.getMaxPriceProductBySource(userId, mo.getThisMonth().getStart().toDate(), fromSource);

        UserPersonalPlayWayVO.PlayWay playWay = new UserPersonalPlayWayVO.PlayWay();
        playWay.setToday(today);
        playWay.setWeek(week);
        playWay.setMonth(month);

        return playWay;
    }

    @Deprecated
    private UserPersonalPlayWayVO.PlayWay getMaxPriceProductOfBox(Integer userId, RecentDateMO mo, List<String> typeList) {
        ProductSimpleVO today = userMessagePlusMapper.getMaxPriceProductOfBox(userId, mo.getToday().getStart().toDate(), typeList);
        ProductSimpleVO week = userMessagePlusMapper.getMaxPriceProductOfBox(userId, mo.getThisWeek().getStart().toDate(), typeList);
        ProductSimpleVO month = userMessagePlusMapper.getMaxPriceProductOfBox(userId, mo.getThisMonth().getStart().toDate(), typeList);

        UserPersonalPlayWayVO.PlayWay playWay = new UserPersonalPlayWayVO.PlayWay();
        playWay.setToday(today);
        playWay.setWeek(week);
        playWay.setMonth(month);

        return playWay;
    }

}
