package com.csgo.web.request.user;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserMessageRecordRequest extends PageRequest {

    private int userId;
}
