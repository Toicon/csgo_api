package com.csgo.web.response.mine;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class MinePrizeResponse {
    //用户id
    private Integer userId;
    //活动id
    private Integer activityId;
    //第几关
    private Integer level;
    //闯关状态(0:放弃，1:通过，2:不通过)
    private Integer passState;
    //账号
    private String userName;
    //奖品商品id
    private Integer giftProductId;
    //奖励名称
    private String prizeName;
    //奖励价格
    private BigDecimal prizePrice;
    //支付金额
    private BigDecimal payPrice;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
}
