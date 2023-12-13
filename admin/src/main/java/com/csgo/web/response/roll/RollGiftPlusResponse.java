package com.csgo.web.response.roll;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RollGiftPlusResponse {
    private Integer id;
    private Integer giftProductId;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private String productname;
    private String img;
    private String grade;
    private Integer rollId;
    private Integer userId;
    private String userName;
    private Date ut;
    private Date ct;
}