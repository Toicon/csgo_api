package com.csgo.web.response.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class UserMessageResponse {
    private Integer id;
    private Integer userId;
    private String gameName;
    private String giftType;
    private String productName;
    private Date drawDare;
    private String state;
    private Integer giftProductId;
    private String knapsackState;
    private BigDecimal money;
    private String img;
    private String giftStatus;
    private String exteriorName;
}
