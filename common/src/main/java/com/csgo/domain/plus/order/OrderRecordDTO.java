package com.csgo.domain.plus.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class OrderRecordDTO {
    private Integer id;

    private String orderNum;

    private String orderStatus;

    private BigDecimal orderAmount;

    private BigDecimal paidAmount;

    private Integer userId;

    private String userPhone;

    private Date createTime;

    private Date updateTime;

    private Date paidTime;
}