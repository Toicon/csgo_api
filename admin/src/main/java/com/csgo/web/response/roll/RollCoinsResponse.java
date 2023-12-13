package com.csgo.web.response.roll;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class RollCoinsResponse {
    private Integer id;
    private String rollName;
    private BigDecimal amount;
    private String userName;
    private String img;
    private Date ct;
}
