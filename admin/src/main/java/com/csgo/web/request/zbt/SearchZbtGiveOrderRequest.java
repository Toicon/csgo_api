package com.csgo.web.request.zbt;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/7/19
 */
@Setter
@Getter
public class SearchZbtGiveOrderRequest extends PageRequest {
    private String name;
    private String status;
    private String cb;
}
