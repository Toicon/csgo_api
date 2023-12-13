package com.csgo.service.statistics;

import com.csgo.constants.UserConstants;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.model.StatisticsOldUserCountVO;
import com.csgo.model.StatisticsUserCountDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class UserCountStatisticsService {

    private static final Integer NULL_DEPT_KEY = Integer.MIN_VALUE;

    private final UserPlusMapper userPlusMapper;
    private final UserMessagePlusMapper userMessagePlusMapper;

    public Map<Integer, List<StatisticsOldUserCountVO>> getDeptOldUserMap(String createDate) {
        List<StatisticsUserCountDTO> list = userPlusMapper.countOldUserByCreateDate(createDate);

        List<StatisticsOldUserCountVO> voList = list.stream().map(i -> {
            StatisticsOldUserCountVO vo = new StatisticsOldUserCountVO();
            vo.setUserCount(i.getUserCount());
            vo.setDeptId(i.getDeptId() == null ? NULL_DEPT_KEY : i.getDeptId());
            vo.setCreateDateTime(new DateTime(i.getCreateDate()));
            return vo;
        }).collect(Collectors.toList());

        Map<Integer, List<StatisticsOldUserCountVO>> map = voList.stream().collect(Collectors.groupingBy(StatisticsOldUserCountVO::getDeptId));
        for (Map.Entry<Integer, List<StatisticsOldUserCountVO>> entry : map.entrySet()) {
            entry.getValue().sort(Comparator.comparing(StatisticsOldUserCountVO::getCreateDateTime));
        }
        return map;
    }

    public Integer getOldUserCountByDeptId(Map<Integer, List<StatisticsOldUserCountVO>> deptOldUserMap, Integer deptId, String date) {
        Integer count = 0;

        List<StatisticsOldUserCountVO> list = deptOldUserMap.get(deptId);
        if (!CollectionUtils.isEmpty(list)) {
            DateTime dateTime = new DateTime(date);
            for (StatisticsOldUserCountVO item : list) {
                if (item.getCreateDateTime().isBefore(dateTime)) {
                    count += item.getUserCount();
                }
            }
        }

        if (deptId.equals(UserConstants.NO_DEPART_ID)) {
            count += getOldUserCountByDeptId(deptOldUserMap, NULL_DEPT_KEY, date);
        }
        return count;
    }

}
