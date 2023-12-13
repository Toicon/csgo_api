package com.csgo.web.request.statistical;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huanghunbao
 */
@Getter
@Setter
public class SearchStatisticalRequest extends PageRequest {

    private String key;
    private String startDate;
    private String endDate;
    /**
     * 部门id
     */
    private Integer deptId;
}
