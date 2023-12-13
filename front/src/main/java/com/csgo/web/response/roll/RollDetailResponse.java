package com.csgo.web.response.roll;

import com.csgo.domain.plus.roll.RollType;
import com.csgo.domain.response.RollGiftResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class RollDetailResponse {

    private Integer id;
    private String rollName;
    private RollType rollType;
    private int sortId;
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
    private Integer num;
    private Integer anchorUserId;
    private String rollNumber;
    private String rollDesc;
    private String anchorDesc;
    private Boolean roomSwitch;
    private String img;

    private List<RollGiftResponse> products;
    private Integer totalGiftNum;
    private BigDecimal totalPrice;
    private BigDecimal diamondTotalPrice;
    private Integer usernum;
    private long remainingTime;

}
