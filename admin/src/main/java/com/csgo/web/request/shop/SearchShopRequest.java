package com.csgo.web.request.shop;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/7/1
 */
@Setter
@Getter
public class SearchShopRequest extends PageRequest {
    private String keywords;

    private String csgoType;

    private BigDecimal lowPrice;

    private BigDecimal highPrice;

    private String sort;

    private String sortBy;

}
