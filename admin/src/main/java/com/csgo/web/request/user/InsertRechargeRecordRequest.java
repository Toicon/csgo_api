package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class InsertRechargeRecordRequest {

    private int userId;
    private int flag;
    private String tag;
    private BigDecimal price;
    private String orderNum;
    private String remark;
    @NotNull
    private String style;
}
