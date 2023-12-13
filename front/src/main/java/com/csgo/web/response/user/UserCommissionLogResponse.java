package com.csgo.web.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class UserCommissionLogResponse {
    private BigDecimal amount;
    private BigDecimal count;
    private BigDecimal proportion;
    private BigDecimal commissionAmount;
    private Integer status;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date settlementTime;
}
