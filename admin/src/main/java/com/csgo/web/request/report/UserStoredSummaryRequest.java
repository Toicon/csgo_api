package com.csgo.web.request.report;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Data;

/**
 * 用户储值收支汇总请求参数
 *
 * @author admin
 */
@Data
public class UserStoredSummaryRequest extends PageRequest {
    private String startDate;
    private String endDate;
}
