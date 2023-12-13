package com.csgo.web.request.withdraw;

import com.echo.framework.platform.web.request.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by admin on 2021/4/29
 */
@Setter
@Getter
public class SearchWithdrawPropRequest extends PageRequest {
    private String userName;
    //账号类型
    private Integer flag;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
}
