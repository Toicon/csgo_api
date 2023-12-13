package com.csgo.web.request.shop;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/7/1
 */
@Setter
@Getter
public class SearchProductRequest extends PageRequest {
    private String keywords;
}
