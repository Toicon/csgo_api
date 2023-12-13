package com.csgo.service.report;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.report.GoodDeliverySummaryDto;
import com.csgo.domain.report.GoodShipmentSummaryDto;
import com.csgo.domain.report.UserBalanceDTO;
import com.csgo.domain.report.UserStoredSummaryDto;
import com.csgo.exception.ServiceErrorException;
import com.csgo.mapper.ReportMapper;
import com.csgo.service.UserBalancePlusService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.PageInfo;
import com.csgo.util.DateUtils;
import com.csgo.util.DateUtilsEx;
import com.csgo.util.StringUtil;
import com.csgo.web.request.report.UserStoredSummaryRequest;
import com.csgo.web.response.report.UserStoredSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 报表服务
 */
@Service
@Slf4j
public class ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserBalancePlusService userBalancePlusService;

    /**
     * 用户储值收支汇总
     *
     * @param request
     * @return
     */
    public List<UserStoredSummaryResponse> userStoredSummary(UserStoredSummaryRequest request) {
        if (StringUtil.isEmpty(request.getStartDate())) {
            throw new ServiceErrorException("开始日期不能为空");
        }
        if (StringUtil.isEmpty(request.getEndDate())) {
            throw new ServiceErrorException("截止日期不能为空");
        }
        Date startDate = DateUtils.stringToDate(request.getStartDate());
        Date endDate = DateUtils.stringToDate(request.getEndDate());
        BigDecimal day = DateUtilsEx.getDifferDay(startDate, endDate);
        if (day.compareTo(BigDecimal.valueOf(30L)) > 0) {
            throw new ServiceErrorException("开始日期与截止日期相差不能超过31天");
        }
        return this.toUserStoredSummary(request.getStartDate(), request.getEndDate());
    }

    /**
     * 商品道具出货统计表
     *
     * @param request
     * @return
     */
    public PageInfo<GoodShipmentSummaryDto> pageGoodShipmentSummary(UserStoredSummaryRequest request) {
        if (StringUtil.isEmpty(request.getStartDate())) {
            throw new ServiceErrorException("开始日期不能为空");
        }
        if (StringUtil.isEmpty(request.getEndDate())) {
            throw new ServiceErrorException("截止日期不能为空");
        }
        Date startDate = DateUtils.stringToDate(request.getStartDate());
        Date endDate = DateUtils.stringToDate(request.getEndDate());
        BigDecimal day = DateUtilsEx.getDifferDay(startDate, endDate);
        if (day.compareTo(BigDecimal.valueOf(30L)) > 0) {
            throw new ServiceErrorException("开始日期与截止日期相差不能超过31天");
        }
        Page<GoodShipmentSummaryDto> page = new Page<>(request.getPageIndex(), request.getPageSize());
        Page<GoodShipmentSummaryDto> list = reportMapper.sumGoodShipment(page, request.getStartDate(), request.getEndDate() + GlobalConstants.END_TIME);
        return new PageInfo<>(list);
    }

    /**
     * ZBT平台道具发货统计表
     *
     * @param request
     * @return
     */
    public PageInfo<GoodDeliverySummaryDto> sumZbtGoodDelivery(UserStoredSummaryRequest request) {
        if (StringUtil.isEmpty(request.getStartDate())) {
            throw new ServiceErrorException("开始日期不能为空");
        }
        if (StringUtil.isEmpty(request.getEndDate())) {
            throw new ServiceErrorException("截止日期不能为空");
        }
        Date startDate = DateUtils.stringToDate(request.getStartDate());
        Date endDate = DateUtils.stringToDate(request.getEndDate());
        BigDecimal day = DateUtilsEx.getDifferDay(startDate, endDate);
        if (day.compareTo(BigDecimal.valueOf(30L)) > 0) {
            throw new ServiceErrorException("开始日期与截止日期相差不能超过31天");
        }
        Page<GoodDeliverySummaryDto> page = new Page<>(request.getPageIndex(), request.getPageSize());
        Page<GoodDeliverySummaryDto> list = reportMapper.sumZbtGoodDelivery(page, request.getStartDate(), request.getEndDate() + GlobalConstants.END_TIME);
        return new PageInfo<>(list);
    }
    
    /**
     * IG平台道具发货统计表
     *
     * @param request
     * @return
     */
    public PageInfo<GoodDeliverySummaryDto> sumIgGoodDelivery(UserStoredSummaryRequest request) {
        if (StringUtil.isEmpty(request.getStartDate())) {
            throw new ServiceErrorException("开始日期不能为空");
        }
        if (StringUtil.isEmpty(request.getEndDate())) {
            throw new ServiceErrorException("截止日期不能为空");
        }
        Date startDate = DateUtils.stringToDate(request.getStartDate());
        Date endDate = DateUtils.stringToDate(request.getEndDate());
        BigDecimal day = DateUtilsEx.getDifferDay(startDate, endDate);
        if (day.compareTo(BigDecimal.valueOf(30L)) > 0) {
            throw new ServiceErrorException("开始日期与截止日期相差不能超过31天");
        }
        Page<GoodDeliverySummaryDto> page = new Page<>(request.getPageIndex(), request.getPageSize());
        Page<GoodDeliverySummaryDto> list = reportMapper.sumIgGoodDelivery(page, request.getStartDate(), request.getEndDate() + GlobalConstants.END_TIME);
        return new PageInfo<>(list);
    }

    /**
     * 用户储值收支汇总--获取两个日期间的对象集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private List<UserStoredSummaryResponse> initUserStoredSummary(Date startDate, Date endDate) {
        List<UserStoredSummaryResponse> userStoredSummaryResponseList = new ArrayList<>();
        List<Date> betweenDates = DateUtils.getBetweenDates(startDate, endDate);
        List<Date> dates = betweenDates.stream().distinct().collect(Collectors.toList());
        dates.forEach(date -> {
            UserStoredSummaryResponse userStoredSummaryResponse = new UserStoredSummaryResponse();
            userStoredSummaryResponse.setDate(DateUtils.toStringDate(date));
            userStoredSummaryResponseList.add(userStoredSummaryResponse);
        });
        return userStoredSummaryResponseList;
    }

    private List<UserStoredSummaryResponse> toUserStoredSummary(String startDate, String endDate) {
        //储值实付
        List<UserStoredSummaryDto> sumDayPaid = reportMapper.sumDayPaid(startDate, endDate + GlobalConstants.END_TIME);
        Map<String, BigDecimal> sumDayPaidMap = sumDayPaid.stream().collect(Collectors.toMap(UserStoredSummaryDto::getDate, UserStoredSummaryDto::getAmount));
        //赠送卡值
        List<UserStoredSummaryDto> sumDayExtra = reportMapper.sumDayExtra(startDate, endDate + GlobalConstants.END_TIME);
        Map<String, BigDecimal> sumDayExtraMap = sumDayExtra.stream().collect(Collectors.toMap(UserStoredSummaryDto::getDate, UserStoredSummaryDto::getAmount));
        //红包收入
        List<UserStoredSummaryDto> sumDayRed = reportMapper.sumDayRed(startDate, endDate + GlobalConstants.END_TIME);
        Map<String, BigDecimal> sumDayRedMap = sumDayRed.stream().collect(Collectors.toMap(UserStoredSummaryDto::getDate, UserStoredSummaryDto::getAmount));
        //CDK收入
        List<UserStoredSummaryDto> sumDayCdk = reportMapper.sumDayCdk(startDate, endDate + GlobalConstants.END_TIME);
        Map<String, BigDecimal> sumDayCdkMap = sumDayCdk.stream().collect(Collectors.toMap(UserStoredSummaryDto::getDate, UserStoredSummaryDto::getAmount));
        //返回结果
        List<UserStoredSummaryResponse> dtoList = initUserStoredSummary(DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate));
        AtomicReference<BigDecimal> sumPaidAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> sumExtraAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> sumPropSaleAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> sumRedAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> sumCdkAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> sumMallExchangeAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> sumLuckyBoxAmount = new AtomicReference<>(BigDecimal.ZERO);
        if (!CollectionUtils.isEmpty(dtoList)) {
            for (int idx = 0; idx < dtoList.size(); idx++) {
                UserStoredSummaryResponse e = dtoList.get(idx);
                if (!CollectionUtils.isEmpty(sumDayPaidMap) && sumDayPaidMap.containsKey(e.getDate())) {
                    e.setPaidAmount(sumDayPaidMap.get(e.getDate()));
                } else {
                    e.setPaidAmount(BigDecimal.ZERO);
                }
                if (!CollectionUtils.isEmpty(sumDayExtraMap) && sumDayExtraMap.containsKey(e.getDate())) {
                    e.setExtraAmount(sumDayExtraMap.get(e.getDate()));
                } else {
                    e.setExtraAmount(BigDecimal.ZERO);
                }
                //道具出售
                BigDecimal sumDayPropSale = reportMapper.sumDayPropSale(e.getDate(), e.getDate() + GlobalConstants.END_TIME);
                if (sumDayPropSale != null) {
                    e.setPropSaleAmount(sumDayPropSale);
                } else {
                    e.setPropSaleAmount(BigDecimal.ZERO);
                }
                if (!CollectionUtils.isEmpty(sumDayRedMap) && sumDayRedMap.containsKey(e.getDate())) {
                    e.setRedAmount(sumDayRedMap.get(e.getDate()));
                } else {
                    e.setRedAmount(BigDecimal.ZERO);
                }
                if (!CollectionUtils.isEmpty(sumDayCdkMap) && sumDayCdkMap.containsKey(e.getDate())) {
                    e.setCdkAmount(sumDayCdkMap.get(e.getDate()));
                } else {
                    e.setCdkAmount(BigDecimal.ZERO);
                }
                //商城兑换
                BigDecimal sumDayMallExchange = reportMapper.sumDayMallExchange(e.getDate(), e.getDate() + GlobalConstants.END_TIME);
                if (sumDayMallExchange != null) {
                    e.setMallExchangeAmount(sumDayMallExchange);
                } else {
                    e.setMallExchangeAmount(BigDecimal.ZERO);
                }

                //幸运宝箱
                BigDecimal sumDayLuckyBox = reportMapper.sumDayLuckyBox(e.getDate(), e.getDate() + GlobalConstants.END_TIME);
                if (sumDayLuckyBox != null) {
                    e.setLuckyBoxAmount(sumDayLuckyBox);
                } else {
                    e.setLuckyBoxAmount(BigDecimal.ZERO);
                }

                //本期余额
                UserBalanceDTO userBalanceDTO = userBalancePlusService.countBalance(e.getDate() + GlobalConstants.END_TIME);
                if (userBalanceDTO != null) {
                    e.setCurrentBalance(userBalanceDTO.getBalanceAmount());
                    e.setCurrentDiamondBalance(userBalanceDTO.getDiamondAmount());
                } else {
                    e.setCurrentBalance(BigDecimal.ZERO);
                    e.setCurrentDiamondBalance(BigDecimal.ZERO);
                }
                if (idx == 0) {
                    //上期余额
                    Date date = DateUtils.stringToDate(e.getDate());
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(date);//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                    Date periodDate = calendar.getTime();
                    String periodDateStr = DateUtils.toStringDate(periodDate);
                    UserBalanceDTO periodUserBalanceDTO = userBalancePlusService.countBalance(periodDateStr + GlobalConstants.END_TIME);
                    if (periodUserBalanceDTO != null) {
                        e.setPeriodBalance(periodUserBalanceDTO.getBalanceAmount());
                        e.setPeriodDiamondBalance(periodUserBalanceDTO.getDiamondAmount());
                    } else {
                        e.setPeriodBalance(BigDecimal.ZERO);
                        e.setPeriodDiamondBalance(BigDecimal.ZERO);
                    }
                } else {
                    e.setPeriodBalance(dtoList.get(idx - 1).getCurrentBalance());
                    e.setPeriodDiamondBalance(dtoList.get(idx - 1).getCurrentDiamondBalance());
                }
                if (e.getPaidAmount() != null) {
                    sumPaidAmount.set(sumPaidAmount.get().add(e.getPaidAmount()));
                }
                if (e.getExtraAmount() != null) {
                    sumExtraAmount.set(sumExtraAmount.get().add(e.getExtraAmount()));
                }
                if (e.getPropSaleAmount() != null) {
                    sumPropSaleAmount.set(sumPropSaleAmount.get().add(e.getPropSaleAmount()));
                }
                if (e.getRedAmount() != null) {
                    sumRedAmount.set(sumRedAmount.get().add(e.getRedAmount()));
                }
                if (e.getCdkAmount() != null) {
                    sumCdkAmount.set(sumCdkAmount.get().add(e.getCdkAmount()));
                }
                if (e.getMallExchangeAmount() != null) {
                    sumMallExchangeAmount.set(sumMallExchangeAmount.get().add(e.getMallExchangeAmount()));
                }
                if (e.getLuckyBoxAmount() != null) {
                    sumLuckyBoxAmount.set(sumLuckyBoxAmount.get().add(e.getLuckyBoxAmount()));
                }

            }
            UserStoredSummaryResponse totalResponse = new UserStoredSummaryResponse();
            totalResponse.setPaidAmount(sumPaidAmount.get());
            totalResponse.setPropSaleAmount(sumPropSaleAmount.get());
            totalResponse.setExtraAmount(sumExtraAmount.get());
            totalResponse.setRedAmount(sumRedAmount.get());
            totalResponse.setCdkAmount(sumCdkAmount.get());
            totalResponse.setMallExchangeAmount(sumMallExchangeAmount.get());
            totalResponse.setLuckyBoxAmount(sumLuckyBoxAmount.get());
            totalResponse.setDate("汇总");
            dtoList.add(totalResponse);
        }
        return dtoList;
    }

}
