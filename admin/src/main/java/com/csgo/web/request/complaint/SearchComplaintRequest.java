package com.csgo.web.request.complaint;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2021/6/17
 */
@Setter
@Getter
public class SearchComplaintRequest extends PageRequest {
    private String type;

    private String telephone;
}
