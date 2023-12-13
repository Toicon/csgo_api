package com.csgo.domain.plus.withdraw;

import com.csgo.domain.enums.WithdrawPropStatus;
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
public class WithdrawPropDTO {
    private int id;
    private String userName;
    private String name;
    private int flag;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date drewDate;
    private WithdrawPropStatus status;
    private String productNames;
    private String giftPrices;
    private String descriptions;
    private BigDecimal totalAmount;
    private String ub;
    private Date ut;
}
