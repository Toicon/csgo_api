package com.csgo.web.request.shop;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/7/1
 */
@Setter
@Getter
public class ShopRequest {
    private Integer giftProductId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Boolean hidden;
}
