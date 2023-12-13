package com.csgo.model;

import lombok.Data;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author admin
 */
@Data
public class StatisticsUserBalanceDTO {

    private Date createDate;

    private DateTime createDateTime;

    private Integer deptId;

    //余额(V币)
    private BigDecimal balanceAmount;

    //余额(钻石)
    private BigDecimal diamondAmount;

}
