package com.csgo.web.request.envelop;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2021/4/29
 */
@Getter
@Setter
public class EditRedEnvelopRequest {

    private String name;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal limitAmount;

    private Integer num;

    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveEndTime;

    private boolean auto;

    private String status;

    @Min(value = 1, message = "The timeInterval must gt 0.")
    private int timeInterval;

    private boolean showNum = true;
}
