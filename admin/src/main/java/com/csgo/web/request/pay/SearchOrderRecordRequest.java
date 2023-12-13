package com.csgo.web.request.pay;

import com.echo.framework.platform.web.request.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2021/7/19
 */
@Setter
@Getter
public class SearchOrderRecordRequest extends PageRequest {

    private String name;

    private String state;

    private String orderNum;

    private String flag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;
}
