package com.csgo.web.request.report;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchAnchorStatisticsRequest extends PageRequest {
    private String startDate;
    private String endDate;
    private String name;
    private String telephone;
    private String orderRecharge;
    /**
     * 部门id
     */
    private Integer deptId;
}
