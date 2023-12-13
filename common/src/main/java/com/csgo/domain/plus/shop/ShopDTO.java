package com.csgo.domain.plus.shop;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/7/1
 */
@Setter
@Getter
public class ShopDTO {
    private Integer id;
    private Integer giftProductId;
    private String name;
    private String imgUrl;
    private BigDecimal price;
    private Integer stock;
    private Boolean hidden;
    private Date ct;

    private String csgoType;
}
