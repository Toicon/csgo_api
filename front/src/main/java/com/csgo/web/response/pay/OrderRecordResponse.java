package com.csgo.web.response.pay;

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
public class OrderRecordResponse {
    private Integer id;
    private String orderNum;
    private String orderStatus;
    private BigDecimal orderAmount;
    private BigDecimal paidAmount;
    private BigDecimal extraPrice;
    private Integer userId;
    private String userPhone;
    private String style;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Date updateTime;
    private Date paidTime;
}
