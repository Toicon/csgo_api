package com.csgo.web.request.operation;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchOperationLogRequest extends PageRequest {
    private String userName;
    private String keywords;
    private Date startDate;
    private Date endDate;
}
