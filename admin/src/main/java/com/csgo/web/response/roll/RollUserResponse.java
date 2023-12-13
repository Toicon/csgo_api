package com.csgo.web.response.roll;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class RollUserResponse {

    private String userName;
    private String name;
    private String flag;
    private BigDecimal rechargeAmount;
    private BigDecimal withdrawAmount;
}
