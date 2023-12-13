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
public class ShopOrderDTO {
    private String userName;
    private String name;
    private Date ct;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private String giftProductName;
    private BigDecimal price;
}
