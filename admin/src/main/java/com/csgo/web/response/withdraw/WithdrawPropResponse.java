package com.csgo.web.response.withdraw;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Setter
@Getter
public class WithdrawPropResponse {
    private int id;
    private String userName;
    private String name;
    private int flag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date drewDate;
    private String productNames;
    private String giftPrices;
    private String descriptions;
    private BigDecimal totalAmount;
    private BigDecimal zbtPrice;
    private BigDecimal rechargeAmount;
    private BigDecimal userRecharge;
    private BigDecimal userPropAmount;
    private String status;
    private String description;
    private String ub;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ut;
}
