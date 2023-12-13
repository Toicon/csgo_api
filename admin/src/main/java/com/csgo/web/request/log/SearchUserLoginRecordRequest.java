package com.csgo.web.request.log;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserLoginRecordRequest extends PageRequest {
    /**
     * 账号
     */
    private String userName;
    /**
     * IP地址
     */
    private String ip;
}
