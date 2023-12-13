package com.csgo.web.request.code;

import com.echo.framework.platform.web.request.PageRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchActivationCodeRequest extends PageRequest {

    private String cdKey;
    private String userName;
}
