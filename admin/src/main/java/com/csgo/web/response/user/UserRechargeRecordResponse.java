package com.csgo.web.response.user;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class UserRechargeRecordResponse {

    private String userName;
    private Integer flag;
    private BigDecimal price;
    private BigDecimal balance;
    private String orderNum;
    private String channelOrderNum;
    private String remark;
    private String cb;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
