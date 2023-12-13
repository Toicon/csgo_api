package com.csgo.web.controller.statistical;

import com.csgo.domain.report.StatisticsDTO;
import com.csgo.domain.report.UserBalanceDTO;
import com.csgo.model.StatisticsOldUserCountVO;
import com.csgo.model.StatisticsUserBalanceDTO;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserService;
import com.csgo.service.order.OrderRecordService;
import com.csgo.service.statistics.GlobalStatisticsService;
import com.csgo.service.statistics.UserBalanceStatisticsService;
import com.csgo.service.statistics.UserCountStatisticsService;
import com.csgo.support.GlobalConstants;
import com.csgo.util.ExcelUtils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.statistical.SearchStatisticalRequest;
import com.csgo.web.response.statistical.StatisticalResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huanghunbao
 * 统计类
 */
@Api
@RequestMapping("/statistical/global")
@RequiredArgsConstructor
public class GlobalStatisticalController extends BackOfficeController {

    private static final String ANCHOR_STATISTICS = "全局数据.xls";

    @Autowired
    private UserService userService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private UserMessageRecordService userMessageRecordService;

    @Autowired
    private UserCountStatisticsService userCountStatisticsService;
    @Autowired
    private UserBalanceStatisticsService userBalanceStatisticsService;

    @Autowired
    private GlobalStatisticsService globalStatisticsService;

    @PostMapping("/queryCount")
    @Log(desc = "查询全局统计")
    public BaseResponse<List<StatisticalResponse>> queryCount(@Valid @RequestBody SearchStatisticalRequest request) {
        List<StatisticalResponse> list = to(request.getDeptId(), request.getStartDate(), request.getEndDate());

        return BaseResponse.<List<StatisticalResponse>>builder().data(list).get();
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

    /**
     * @see StatisticalController#to(Integer, String, String) (SearchStatisticalRequest)
     */
    private List<StatisticalResponse> to(Integer deptId, String startDate, String endDate) {
        Map<Integer, List<StatisticsOldUserCountVO>> deptOldUserMap = userCountStatisticsService.getDeptOldUserMap(endDate);
        Map<Integer, List<StatisticsUserBalanceDTO>> deptBalanceMap = userBalanceStatisticsService.getDeptBalanceMap(endDate);

        String endDateFinish = endDate + GlobalConstants.END_TIME;

        List<StatisticsDTO> addCountDate = userService.countAddUser(startDate, endDateFinish);
        List<StatisticsDTO> addBalance = orderRecordService.dailyStatistical(startDate, endDateFinish);
        List<StatisticsDTO> countActive = orderRecordService.countActive(startDate, endDateFinish);
        List<StatisticsDTO> countOpenCount = userMessageRecordService.countOpenCount(startDate, endDateFinish);
        List<StatisticsDTO> activeCount = userMessageRecordService.countActiveCount(startDate, endDateFinish);

        List<UserStatistical> dtoList = globalStatisticsService.initialize(com.csgo.util.DateUtils.stringToDate(startDate), com.csgo.util.DateUtils.stringToDate(endDate));
        // 只统计叶子节点数据
        List<UserStatistical> leafList = dtoList.stream().filter(a -> !a.isHasChild()).collect(Collectors.toList());

        List<UserStatistical> childrenStatistical = leafList.parallelStream().peek(e -> {
            StatisticsDTO addCountStatisticsDTO = addCountDate.stream()
                    .filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId()))
                    .findFirst()
                    .orElse(null);
            // 每日新增
            if (addCountStatisticsDTO != null) {
                e.setAddCount(addCountStatisticsDTO.getCount());
            }
            // 每日充值余额
            addBalance.stream()
                    .filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId()))
                    .findFirst()
                    .ifPresent(item -> e.setAddBalance(item.getAmount()));
            // 老用户
            Integer oldCount = userCountStatisticsService.getOldUserCountByDeptId(deptOldUserMap, e.getDeptId(), e.getDate());
            e.setOldCount(oldCount);
            // 日活跃付费用户
            StatisticsDTO countActiveStatisticsDTO = countActive.stream()
                    .filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId()))
                    .findFirst()
                    .orElse(null);
            if (countActiveStatisticsDTO != null) {
                e.setActiveNowCount(countActiveStatisticsDTO.getCount());
                if (null != e.getAddBalance() && e.getAddBalance().compareTo(BigDecimal.ZERO) > 0) {
                    e.setAPRU(e.getAddBalance().divide(new BigDecimal(countActiveStatisticsDTO.getCount()), 2, RoundingMode.HALF_UP));
                }
            }
            //开箱次数
            countOpenCount.stream()
                    .filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId()))
                    .findFirst()
                    .ifPresent(item -> e.setOpenCount(item.getCount()));
            //活跃用户数
            activeCount.stream()
                    .filter(a -> a.getDate().equals(e.getDate()) && a.getDeptId().equals(e.getDeptId()))
                    .findFirst()
                    .ifPresent(item -> e.setActiveCount(item.getCount()));

            // 用户余额
            UserBalanceDTO balance = userBalanceStatisticsService.getDeptBalance(deptBalanceMap, e.getDeptId(), e.getDate() + GlobalConstants.END_TIME);

            // V币余额
            e.setBalance(balance.getBalanceAmount());

            // 砖石余额
            e.setDiamondBalance(balance.getDiamondAmount());

            // 背包余额
            BigDecimal backpackBalance = userBalanceStatisticsService.getBackpackBalance(e.getDeptId(), e.getDate() + GlobalConstants.END_TIME);
            e.setBackpackBalance(backpackBalance);
        }).collect(Collectors.toList());

        List<StatisticalResponse> childrenResponse = childrenStatistical.parallelStream().map(e -> {
            StatisticalResponse statisticalResponse = to(e);

            // 钻石余额 = 钻石余额 + 背包余额
            statisticalResponse.setDiamondBalance(statisticalResponse.getDiamondBalance().add(e.getBackpackBalance()));
            statisticalResponse.setDate(e.getDate());
            return statisticalResponse;
        }).collect(Collectors.toList());

        // 处理父节点
        List<UserStatistical> parentList = dtoList.stream().filter(UserStatistical::isHasChild)
                .sorted(Comparator.comparing(UserStatistical::getDeptId).reversed())
                .collect(Collectors.toList());

        List<StatisticalResponse> parentResponse = parentList.stream().map(e -> {
            List<StatisticalResponse> childResponse = childrenResponse.stream()
                    .filter(a -> a.getDate().equals(e.getDate()) && a.getParentId().equals(e.getDeptId()))
                    .collect(Collectors.toList());
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
                if (sumAddBalance.compareTo(BigDecimal.ZERO) > 0) {
                    statisticalResponse.setAPRU(sumAddBalance.divide(new BigDecimal(sumActiveNowCount), 2, RoundingMode.HALF_UP));
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
            List<StatisticalResponse> childResponse = parentResponse.stream()
                    .filter(a -> a.getDate().equals(parent.getDate()) && a.getParentId().equals(parent.getDeptId()))
                    .collect(Collectors.toList());
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
                    parent.setAPRU(parent.getAddBalance().divide(new BigDecimal(parent.getActiveNowCount()), 2, RoundingMode.HALF_UP));
                } else {
                    parent.setAPRU(BigDecimal.ZERO);
                }
            } else {
                parent.setAPRU(BigDecimal.ZERO);
            }
        }
        childrenResponse.addAll(parentResponse);

        Integer userId = siteContext.getCurrentUser().getId();
        globalStatisticsService.filterAdminReport(userId, childrenResponse);

        if (deptId != null) {
            return childrenResponse.stream()
                    .filter(a -> a.getDeptId().equals(deptId))
                    .collect(Collectors.toList());
        }

        return childrenResponse.stream()
                .sorted(Comparator.comparing(StatisticalResponse::getDate)
                .thenComparing(StatisticalResponse::getDeptId))
                .collect(Collectors.toList());
    }

    private StatisticalResponse to(UserStatistical userStatistical) {
        StatisticalResponse response = new StatisticalResponse();
        BeanUtils.copyProperties(userStatistical, response);
        return response;
    }


}
