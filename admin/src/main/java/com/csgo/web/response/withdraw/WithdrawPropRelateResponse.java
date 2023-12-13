package com.csgo.web.response.withdraw;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Setter
@Getter
public class WithdrawPropRelateResponse {
    private int id;
    private String giftProductName;
    private BigDecimal price;
    private String status;
    private String description;
    private String message;
}
