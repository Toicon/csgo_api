package com.csgo.web.request.accessory;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchLuckyProductRequest extends PageRequest {

    private String keywords;
    private String csgoType;
    private BigDecimal lowPrice;
    private BigDecimal highPrice;
    private int priceSort;
    private int typeId;
}
