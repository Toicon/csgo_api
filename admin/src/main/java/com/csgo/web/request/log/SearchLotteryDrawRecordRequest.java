package com.csgo.web.request.log;

import com.echo.framework.platform.web.request.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchLotteryDrawRecordRequest extends PageRequest {

    private String userName;

    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
}
