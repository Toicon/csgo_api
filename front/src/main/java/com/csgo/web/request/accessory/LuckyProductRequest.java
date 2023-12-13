package com.csgo.web.request.accessory;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyProductRequest {

    private int luckyId;
    private BigDecimal luckyValue;
    private BigDecimal price;
}
