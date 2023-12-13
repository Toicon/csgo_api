package com.csgo.web.request.shop;

import com.csgo.condition.shop.SortType;
import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchShopProductRequest extends PageRequest {

    private String giftProductName;
    private SortType sortType;
    private BigDecimal min;
    private BigDecimal max;
    private String exteriorName;
}
