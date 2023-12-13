package com.csgo.web.response.roll;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/6/20
 */
@Setter
@Getter
public class RollResponse {

    private Integer id;
    private String rollName;
    private String rollType;
    private String type;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm", timezone = "GMT+8")
    private Date drawDate;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    private BigDecimal userLimit;
    private String anchorLink;
    private String status;
    private Date ut;
    private Date ct;
    private Integer num;
    private Integer anchorUserId;
    private String rollNumber;
    private String rollDesc;
    private String anchorDesc;
    private String img;
    private Boolean roomSwitch;
    private Boolean joined;

    private Boolean needPassword;
}
