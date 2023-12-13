package com.csgo.web.response.envelop;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class RedEnvelopItemResponse {

    private Integer id;
    private Integer envelopId;
    private String name;
    private int num;
    //参加条件
    private BigDecimal limitAmount;
    //活动开始时间
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveStartTime;
    //活动开始时间
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveEndTime;
    private boolean auto;

    private boolean showNum;

    private String showMsg;

    /**
     * 领取状态（可领取、已领取）
     */
    private Integer receiveStatus;
    /**
     * 距离领取奖励还差金额
     */
    private BigDecimal enoughAmount;
}