package com.csgo.web.response.log;

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
public class LuckyProductDrawRecordResponse {

    private int id;
    private String userName;
    private BigDecimal lucky;
    private String productName;
    private BigDecimal price;
    private BigDecimal rate;
    private boolean hit;
    private String message;
    private BigDecimal profit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
