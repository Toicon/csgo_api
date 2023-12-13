package com.csgo.condition.report;

import com.csgo.condition.Condition;
import com.csgo.domain.report.AnchorStatisticsDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchAnchorStatisticsCondition extends Condition<AnchorStatisticsDTO> {
    private String startDate;
    private String endDate;
    private String name;
    private String telephone;
    private String orderRecharge;
    /**
     * 数据权限
     */
    private String dataScope;
    /**
     * 部门id
     */
    private Integer deptId;
}
