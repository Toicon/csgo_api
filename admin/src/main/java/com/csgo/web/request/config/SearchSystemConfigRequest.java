package com.csgo.web.request.config;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchSystemConfigRequest extends PageRequest {
    private String prefix;
}
