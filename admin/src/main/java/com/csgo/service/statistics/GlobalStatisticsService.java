package com.csgo.service.statistics;

import com.csgo.constants.UserConstants;
import com.csgo.domain.SysDept;
import com.csgo.domain.plus.user.AdminReportFilter;
import com.csgo.mapper.plus.user.AdminReportFilterMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.service.dept.SysDeptService;
import com.csgo.web.controller.statistical.UserStatistical;
import com.csgo.web.response.statistical.StatisticalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalStatisticsService {

    private static final Integer NULL_DEPT_KEY = Integer.MIN_VALUE;

    private final UserPlusMapper userPlusMapper;
    private final UserMessagePlusMapper userMessagePlusMapper;

    private final AdminReportFilterMapper adminReportFilterMapper;

    /**
     * 过滤顶级部门和未归属主播
     */
    public void filterAdminReport(Integer userId, List<StatisticalResponse> childrenResponse) {
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
    }

    private final SysDeptService deptService;

    /**
     * 获取两个日期间的对象集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<UserStatistical> initialize(Date startDate, Date endDate) {
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

}
