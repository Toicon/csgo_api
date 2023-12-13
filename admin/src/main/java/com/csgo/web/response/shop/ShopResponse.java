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
public class ShopResponse {
    private Integer id;
    private Integer giftProductId;
    private String name;
    private String imgUrl;
    private BigDecimal price;
    private Integer stock;
    private Boolean hidden;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
