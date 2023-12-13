package com.csgo.web.request.user;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserMessageRequest extends PageRequest {

    private Integer productKind;

    private Date startTime;
    private Date endTime;
}
