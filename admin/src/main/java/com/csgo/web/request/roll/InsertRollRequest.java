package com.csgo.web.request.roll;

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
public class InsertRollRequest {
    private String rollType;
    private String anchorUserName;
    private String rollName;
    private int sortId;
    private String rollNumber;
    private String img;
    private String password;
    private BigDecimal userLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date drawDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    private String rollDesc;
    private Integer minGrade;
    private Integer maxGrade;
}
