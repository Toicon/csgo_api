package com.csgo.web.response.envelop;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2021/4/29
 */
@Getter
@Setter
public class RedEnvelopResponse {

    private Integer id;

    private String name;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal limitAmount;

    private BigDecimal totalAmount;

    private Integer num;

    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveEndTime;

    private boolean auto;
    private String status;

    private int timeInterval;

    private boolean showNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    private Integer sortId;

}
