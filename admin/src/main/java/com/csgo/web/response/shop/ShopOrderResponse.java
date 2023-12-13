package com.csgo.web.response.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/7/1
 */
@Setter
@Getter
public class ShopOrderResponse {
    private String userName;
    private String name;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private String giftProductName;
    private BigDecimal price;
}
