package com.csgo.web.response.roll;

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
public class RollResponse {

    private Integer id;
    private String rollName;
    private String rollType;
    private Integer sortId;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date drawDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    private String password;
    private BigDecimal userLimit;
    private String anchorLink;
    private String status;
    private Date ut;
    private Date ct;
    private Integer num;
    private Integer anchorUserId;
    private String anchorUserName;
    private String rollNumber;
    private String rollDesc;
    private String anchorDesc;
    private String img;
    private Boolean roomSwitch;
    private Integer userCount;
    //银币件数
    private Integer totalGiftNum;
    //银币奖励
    private BigDecimal totalPrice;
    //V币件数
    private Integer totalXNum;
    //V币奖励
    private BigDecimal totalXPrice;
    private Integer minGrade;
    private Integer maxGrade;
}
