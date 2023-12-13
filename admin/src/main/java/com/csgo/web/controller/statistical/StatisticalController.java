package com.csgo.web.controller.statistical;

import com.csgo.constants.UserConstants;
import com.csgo.domain.SysDept;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.user.AdminReportFilter;
import com.csgo.domain.report.StatisticsDTO;
import com.csgo.domain.report.UserBalanceDTO;
import com.csgo.mapper.plus.user.AdminReportFilterMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.UserBalancePlusService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserService;
import com.csgo.service.dept.SysDeptService;
import com.csgo.service.order.OrderRecordService;
import com.csgo.service.withdraw.WithdrawPropService;
import com.csgo.support.GlobalConstants;
import com.csgo.util.ExcelUtils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.statistical.SearchStatisticalPanelRequest;
import com.csgo.web.request.statistical.SearchStatisticalRequest;
import com.csgo.web.response.statistical.DailyStatisticalResponse;
import com.csgo.web.response.statistical.StatisticalResponse;
import com.csgo.web.support.Log;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.util.Messages;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huanghunbao
 * 统计类
 */
@Api
@RequestMapping("/statistical")
public class StatisticalController extends BackOfficeController {

    private static final String ANCHOR_STATISTICS = "全局数据.xls";
    private static final String ALI_FACE_OPEN_KEY = "openFace";
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private UserBalancePlusService userBalancePlusService;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private WithdrawPropService withdrawPropService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    protected SiteContext siteContext;
    @Autowired
    private SysDeptService deptService;


    @Autowired
    private AdminReportFilterMapper adminReportFilterMapper;

    @PostMapping("/count/user")
    public BaseResponse<Integer> countUserNum(@Valid @RequestBody SearchStatisticalPanelRequest request) {
        return BaseResponse.<Integer>builder().data(userService.countUserNum(request.getStartDate(), request.getEndDate())).get();
    }

    @PostMapping("/count/recharge")
    public BaseResponse<Integer> countRechargeTotal(@Valid @RequestBody SearchStatisticalPanelRequest request) {
        return BaseResponse.<Integer>builder().data(orderRecordService.countRechargeTotal(request.getStartDate(), request.getEndDate())).get();
    }

    @PostMapping("/count/spending")
    public BaseResponse<BigDecimal> countSpendingTotal(@Valid @RequestBody SearchStatisticalPanelRequest request) {
        return BaseResponse.<BigDecimal>builder().data(withdrawPropService.countSpendingTotal(request.getStartDate(), request.getEndDate())).get();
    }

    @PostMapping("/daily/recharge")
    public BaseResponse<DailyStatisticalResponse> dailyRechargeStatistical(@Valid @RequestBody SearchStatisticalPanelRequest request) {
        Date endDate = request.getEndDate();
        if (endDate == null) {
            endDate = new Date();
        }
        Date startDate = request.getStartDate();
        if (startDate == null) {
            startDate = DateUtils.addWeeks(endDate, -1);
        }
        Map<String, BigDecimal> dailyStatisticalMap = orderRecordService.dailyStatistical(startDate, endDate).stream().collect(Collectors.toMap(StatisticsDTO::getDate, StatisticsDTO::getAmount));
        DailyStatisticalResponse response = new DailyStatisticalResponse();
        response.setXAxis(new ArrayList<>());
        response.setSeries(new ArrayList<>());
        Date day = startDate;
        while (day.compareTo(endDate) <= 0) {
            String dayStr = com.csgo.util.DateUtils.toStringDate(day);
            response.getXAxis().add(dayStr);
            response.getSeries().add(Optional.ofNullable(dailyStatisticalMap.get(dayStr)).orElse(BigDecimal.ZERO).toString());
            day = DateUtils.addDays(day, 1);
        }
        return BaseResponse.<DailyStatisticalResponse>builder().data(response).get();
    }

    @PostMapping("/queryCount")
    @Log(desc = "查询全局统计")
    public BaseResponse<List<StatisticalResponse>> queryCount(@Valid @RequestBody SearchStatisticalRequest request) {
        return BaseResponse.<List<StatisticalResponse>>builder().data(to(request.getDeptId(), request.getStartDate(), request.getEndDate())).get();
    }

    @GetMapping(value = "/export")
    @Log(desc = "导出全局统计")
    public BaseResponse<Void> export(@RequestParam("deptId") Integer deptId, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<StatisticalResponse> responses = to(deptId, startDate, endDate);
        HSSFWorkbook workbook = ExcelUtils.getHSSFWorkWithHeaders(responses, StatisticalResponse.class, false);
        ExcelUtils.downFileWithFileName(response, workbook, request, ANCHOR_STATISTICS);

        return BaseResponse.<Void>builder().get();
    }

    private List<StatisticalResponse> to(Integer deptId, String startDate, String endDate) {
        List<StatisticsDTO> addCountDate = userService.countAddUser(startDate, endDate + GlobalConstants.END_TIME);
        List<StatisticsDTO> addBalance = orderRecordService.dailyStatistical(startDate, endDate + GlobalConstants.END_TIME);
        List<StatisticsDTO> countActive = orderRecordService.countActive(startDate, endDate + GlobalConstants.END_TIME);
        List<StatisticsDTO> countOpenCount = userMessageRecordService.countOpenCount(startDate, endDate + GlobalConstants.END_TIME);
        List<StatisticsDTO> activeCount = userMessageRecordService.countActiveCount(startDate, endDate + GlobalConstants.END_TIME);
        List<UserStatistical> dtoList = this.initialize(com.csgo.util.DateUtils.stringToDate(startDate), com.csgo.util.DateUtils.stringToDate(endDate));
        //只统计叶子节点数据
        List<UserStatistical> parentList = dtoList.stream().filter(a -> a.isHasChild() == true).sorted(Comparator.comparing(UserStatistical::getDeptId).reversed()).collect(Collectors.toList());
        List<UserStatistical> leafList = dtoList.stream().filter(a -> a.isHasChild() == false).collect(Collectors.toList());
        List<StatisticalResponse> childrenResponse = leafList.stream().map(e -> {
            StatisticsDTO addCountStatisticsDTO = addCountDate.stream().filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId())).findFirst().orElse(null);
            // 每日新增
            if (addCountStatisticsDTO != null) {
                e.setAddCount(addCountStatisticsDTO.getCount());
            } else {
                e.setAddCount(0);
            }
            // 每日充值余额
            StatisticsDTO addBalanceStatisticsDTO = addBalance.stream().filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId())).findFirst().orElse(null);
            if (addBalanceStatisticsDTO != null) {
                e.setAddBalance(addBalanceStatisticsDTO.getAmount());
            } else {
                e.setAddBalance(BigDecimal.ZERO);
            }
            // 老用户
            e.setOldCount(userService.countOldUser(e.getDeptId(), e.getDate()));
            // 日活跃付费用户
            StatisticsDTO countActiveStatisticsDTO = countActive.stream().filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId())).findFirst().orElse(null);
            if (countActiveStatisticsDTO != null) {
                e.setActiveNowCount(countActiveStatisticsDTO.getCount());
                if (null != e.getAddBalance() && e.getAddBalance().compareTo(BigDecimal.ZERO) > 0) {
                    e.setAPRU(e.getAddBalance().divide(new BigDecimal(countActiveStatisticsDTO.getCount()), 2, BigDecimal.ROUND_HALF_UP));
                } else {
                    e.setAPRU(BigDecimal.ZERO);
                }
            } else {
                e.setAPRU(BigDecimal.ZERO);
            }
            //用户余额
            UserBalanceDTO userBalanceDTO = userBalancePlusService.countBalance(e.getDeptId(), e.getDate() + GlobalConstants.END_TIME);
            //V币余额
            BigDecimal balanceAmount = userBalanceDTO.getBalanceAmount();
            if (balanceAmount != null) {
                e.setBalance(balanceAmount);
            } else {
                e.setBalance(BigDecimal.ZERO);
            }
            //银币余额
            BigDecimal diamondAmount = userBalanceDTO.getDiamondAmount();
            if (diamondAmount != null) {
                e.setDiamondBalance(diamondAmount);
            } else {
                e.setDiamondBalance(BigDecimal.ZERO);
            }
            //开箱次数
            StatisticsDTO countOpenCountStatisticsDTO = countOpenCount.stream().filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId())).findFirst().orElse(null);
            if (countOpenCountStatisticsDTO != null) {
                e.setOpenCount(countOpenCountStatisticsDTO.getCount());
            } else {
                e.setOpenCount(0);
            }
            //活跃用户数
            StatisticsDTO activeCountStatisticsDTO = activeCount.stream().filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId())).findFirst().orElse(null);
            if (activeCountStatisticsDTO != null) {
                e.setActiveCount(activeCountStatisticsDTO.getCount());
            } else {
                e.setActiveCount(0);
            }
            StatisticalResponse statisticalResponse = to(e);
            statisticalResponse.setDate(e.getDate());
            return statisticalResponse;
        }).collect(Collectors.toList());
        List<StatisticalResponse> parentResponse = parentList.stream().map(e -> {
            List<StatisticalResponse> childResponse = childrenResponse.stream().filter(a -> a.getDate().equals(e.getDate()) && a.getParentId().equals(e.getDeptId())).collect(Collectors.toList());
            int sumOpenCount = childResponse.stream().mapToInt(StatisticalResponse::getOpenCount).sum();
            int sumActiveCount = childResponse.stream().mapToInt(StatisticalResponse::getActiveCount).sum();
            int sumAddCount = childResponse.stream().mapToInt(StatisticalResponse::getAddCount).sum();
            int sumOldCount = childResponse.stream().mapToInt(StatisticalResponse::getOldCount).sum();
            int sumActiveNowCount = childResponse.stream().mapToInt(StatisticalResponse::getActiveNowCount).sum();
            BigDecimal sumBalance = childResponse.stream().map(StatisticalResponse::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumDiamondBalance = childResponse.stream().map(StatisticalResponse::getDiamondBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumAddBalance = childResponse.stream().map(StatisticalResponse::getAddBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            StatisticalResponse statisticalResponse = to(e);
            statisticalResponse.setOpenCount(sumOpenCount);
            statisticalResponse.setActiveCount(sumActiveCount);
            statisticalResponse.setAddCount(sumAddCount);
            statisticalResponse.setOldCount(sumOldCount);
            statisticalResponse.setActiveNowCount(sumActiveNowCount);
            statisticalResponse.setBalance(sumBalance);
            statisticalResponse.setDiamondBalance(sumDiamondBalance);
            if (sumActiveNowCount > 0) {
                if (null != sumAddBalance && sumAddBalance.compareTo(BigDecimal.ZERO) > 0) {
                    statisticalResponse.setAPRU(sumAddBalance.divide(new BigDecimal(sumActiveNowCount), 2, BigDecimal.ROUND_HALF_UP));
                } else {
                    statisticalResponse.setAPRU(BigDecimal.ZERO);
                }
            } else {
                statisticalResponse.setAPRU(BigDecimal.ZERO);
            }
            statisticalResponse.setAddBalance(sumAddBalance);
            statisticalResponse.setDate(e.getDate());
            return statisticalResponse;
        }).collect(Collectors.toList());
        for (StatisticalResponse parent : parentResponse) {
            List<StatisticalResponse> childResponse = parentResponse.stream().filter(a -> a.getDate().equals(parent.getDate()) && a.getParentId().equals(parent.getDeptId())).collect(Collectors.toList());
            int sumOpenCount = childResponse.stream().mapToInt(StatisticalResponse::getOpenCount).sum();
            int sumActiveCount = childResponse.stream().mapToInt(StatisticalResponse::getActiveCount).sum();
            int sumAddCount = childResponse.stream().mapToInt(StatisticalResponse::getAddCount).sum();
            int sumOldCount = childResponse.stream().mapToInt(StatisticalResponse::getOldCount).sum();
            int sumActiveNowCount = childResponse.stream().mapToInt(StatisticalResponse::getActiveNowCount).sum();
            BigDecimal sumBalance = childResponse.stream().map(StatisticalResponse::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumDiamondBalance = childResponse.stream().map(StatisticalResponse::getDiamondBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumAddBalance = childResponse.stream().map(StatisticalResponse::getAddBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
            parent.setOpenCount(parent.getOpenCount() + sumOpenCount);
            parent.setActiveCount(parent.getActiveCount() + sumActiveCount);
            parent.setAddCount(parent.getAddCount() + sumAddCount);
            parent.setOldCount(parent.getOldCount() + sumOldCount);
            parent.setActiveNowCount(parent.getActiveNowCount() + sumActiveNowCount);
            parent.setBalance(parent.getBalance().add(sumBalance));
            parent.setDiamondBalance(parent.getDiamondBalance().add(sumDiamondBalance));
            parent.setAddBalance(parent.getAddBalance().add(sumAddBalance));
            if (parent.getActiveNowCount() > 0) {
                if (null != parent.getAddBalance() && parent.getAddBalance().compareTo(BigDecimal.ZERO) > 0) {
                    parent.setAPRU(parent.getAddBalance().divide(new BigDecimal(parent.getActiveNowCount()), 2, BigDecimal.ROUND_HALF_UP));
                } else {
                    parent.setAPRU(BigDecimal.ZERO);
                }
            } else {
                parent.setAPRU(BigDecimal.ZERO);
            }
        }
        childrenResponse.addAll(parentResponse);
        //过滤顶级部门和未归属主播
        Integer userId = siteContext.getCurrentUser().getId();
        List<AdminReportFilter> adminReportFilterList = adminReportFilterMapper.selectList(null);
        if (!CollectionUtils.isEmpty(adminReportFilterList)) {
            List<AdminReportFilter> filters = adminReportFilterList.stream().filter(a -> a.getUserId().equals(userId)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(filters)) {
                if (!CollectionUtils.isEmpty(childrenResponse)) {
                    childrenResponse.removeAll(childrenResponse.stream().filter(a -> a.getDeptId().equals(UserConstants.ROOT_DEPART_ID)).collect(Collectors.toList()));
                    childrenResponse.removeAll(childrenResponse.stream().filter(a -> a.getDeptId().equals(UserConstants.NO_DEPART_ID)).collect(Collectors.toList()));
                }
            }
        }
        if (deptId != null) {
            return childrenResponse.stream().filter(a -> a.getDeptId().equals(deptId)).collect(Collectors.toList());
        } else {
            return childrenResponse.stream().sorted(Comparator.comparing(StatisticalResponse::getDate).thenComparing(StatisticalResponse::getDeptId)).collect(Collectors.toList());
        }
    }

    private StatisticalResponse to(UserStatistical userStatistical) {
        StatisticalResponse response = new StatisticalResponse();
        BeanUtils.copyProperties(userStatistical, response);
        return response;
    }

    /**
     * 获取两个日期间的对象集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private List<UserStatistical> initialize(Date startDate, Date endDate) {
        SysDept dept = new SysDept();
        List<SysDept> deptList = deptService.dataScopeList(dept);
        List<UserStatistical> userStatisticalList = new ArrayList<>();
        List<Date> betweenDates = com.csgo.util.DateUtils.getBetweenDates(startDate, endDate);
        List<Date> dates = betweenDates.stream().distinct().collect(Collectors.toList());
        dates.forEach(date -> {
            if (deptList != null && deptList.size() > 0) {
                for (SysDept sysDept : deptList) {
                    UserStatistical userStatistical = new UserStatistical();
                    userStatistical.setDate(com.csgo.util.DateUtils.toStringDate(date));
                    userStatistical.setParentId(sysDept.getParentId());
                    userStatistical.setHasChild(deptService.hasChild(deptList, sysDept));
                    userStatistical.setDeptId(sysDept.getId());
                    userStatistical.setDeptName(sysDept.getDeptName());
                    userStatisticalList.add(userStatistical);
                }
            }
        });
        return userStatisticalList;
    }

    @PostMapping("/openFace")
    public BaseResponse<String> openFace() {
        String message = "";
        boolean isOpen = false;
        String openFace = redisTemplateFacde.get(getFaceOpenKey());
        if (!StringUtils.isEmpty(openFace) && YesOrNoEnum.NO.getCode().toString().equals(openFace)) {
            isOpen = true;
        }
        if (isOpen) {
            message = "开启成功";
            redisTemplateFacde.set(getFaceOpenKey(), YesOrNoEnum.YES.getCode().toString());
        } else {
            message = "关闭成功";
            redisTemplateFacde.set(getFaceOpenKey(), YesOrNoEnum.NO.getCode().toString());
        }
        return BaseResponse.<String>builder().data(message).get();
    }

    /**
     * 人脸识别是否开启的redis key
     *
     * @return
     */
    private String getFaceOpenKey() {
        return Messages.format("ali:{}", ALI_FACE_OPEN_KEY);
    }
}
