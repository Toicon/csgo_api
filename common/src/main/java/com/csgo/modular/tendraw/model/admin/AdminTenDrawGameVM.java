package com.csgo.modular.tendraw.model.admin;

import com.csgo.framework.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminTenDrawGameVM extends PageParam {

    private Integer userId;

    private String userName;

    private String userNameQ;

    private Date startDate;
    private Date endDate;

    private BigDecimal prizePriceMin;

    private BigDecimal prizePriceMax;

}
