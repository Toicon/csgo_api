package com.csgo.service.statistics;

import com.csgo.constants.UserConstants;
import com.csgo.domain.report.UserBalanceDTO;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.model.StatisticsUserBalanceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceStatisticsService {

    private static final Integer NULL_DEPT_KEY = Integer.MIN_VALUE;

    private final UserPlusMapper userPlusMapper;

    private final UserMessagePlusMapper userMessagePlusMapper;

    public Map<Integer, List<StatisticsUserBalanceDTO>> getDeptBalanceMap(String createDate) {
        List<StatisticsUserBalanceDTO> list = userPlusMapper.countBalanceByCreateDate(createDate);

        List<StatisticsUserBalanceDTO> voList = list.stream().map(i -> {
            StatisticsUserBalanceDTO vo = new StatisticsUserBalanceDTO();
            vo.setBalanceAmount(i.getBalanceAmount());
            vo.setDiamondAmount(i.getDiamondAmount());
            vo.setDeptId(i.getDeptId() == null ? NULL_DEPT_KEY : i.getDeptId());
            vo.setCreateDateTime(new DateTime(i.getCreateDate()));
            return vo;
        }).collect(Collectors.toList());

        Map<Integer, List<StatisticsUserBalanceDTO>> map = voList.stream().collect(Collectors.groupingBy(StatisticsUserBalanceDTO::getDeptId));
        for (Map.Entry<Integer, List<StatisticsUserBalanceDTO>> entry : map.entrySet()) {
            entry.getValue().sort(Comparator.comparing(StatisticsUserBalanceDTO::getCreateDateTime));
        }
        return map;
    }

    public UserBalanceDTO getDeptBalance(Map<Integer, List<StatisticsUserBalanceDTO>> deptOldUserMap, Integer deptId, String date) {
        //余额(钻石)
        BigDecimal diamondAmount = BigDecimal.ZERO;
        //余额(V币)
        BigDecimal balanceAmount = BigDecimal.ZERO;

        List<StatisticsUserBalanceDTO> list = deptOldUserMap.get(deptId);
        if (!CollectionUtils.isEmpty(list)) {
            DateTime dateTime = DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
            for (StatisticsUserBalanceDTO item : list) {
                if (item.getCreateDateTime().isBefore(dateTime)) {
                    diamondAmount = diamondAmount.add(item.getDiamondAmount());
                    balanceAmount = balanceAmount.add(item.getBalanceAmount());
                }
            }
        }
        if (deptId.equals(UserConstants.NO_DEPART_ID)) {
            UserBalanceDTO noDeptBalance = getDeptBalance(deptOldUserMap, NULL_DEPT_KEY, date);
            diamondAmount = diamondAmount.add(noDeptBalance.getDiamondAmount());
            balanceAmount = balanceAmount.add(noDeptBalance.getBalanceAmount());
        }

        UserBalanceDTO result = new UserBalanceDTO();
        result.setBalanceAmount(balanceAmount);
        result.setDiamondAmount(diamondAmount);

        return result;
    }

    public BigDecimal getBackpackBalance(Integer deptId, String endDate) {
        //背包饰品金额(钻石)
        BigDecimal knapsackAmount = BigDecimal.ZERO;
        knapsackAmount = knapsackAmount.add(userMessagePlusMapper.countPrice(deptId, endDate));
        if (deptId.equals(UserConstants.NO_DEPART_ID)) {
            knapsackAmount = knapsackAmount.add(userMessagePlusMapper.countPriceNotOwner(endDate));
        }
        return knapsackAmount;
    }

}
