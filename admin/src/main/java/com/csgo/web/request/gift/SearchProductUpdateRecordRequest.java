package com.csgo.web.request.gift;

import java.util.Date;
import com.echo.framework.platform.web.request.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2021/4/29
 */
@Setter
@Getter
public class SearchProductUpdateRecordRequest extends PageRequest {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
}
