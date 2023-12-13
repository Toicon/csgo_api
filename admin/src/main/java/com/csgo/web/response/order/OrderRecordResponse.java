package com.csgo.web.response.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/5/4
 */
@Setter
@Getter
public class OrderRecordResponse {
    private Integer id;

    private String orderNum;

    private String orderStatus;

    private BigDecimal orderAmount;

    private BigDecimal paidAmount;

    private Integer userId;

    private String userPhone;

    private BigDecimal totalAmount;

    private BigDecimal normalAmount;

    private BigDecimal memberAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paidTime;
}
