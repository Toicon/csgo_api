package com.csgo.web.response.user;

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
public class UserPrizeResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userImg;
    private String userNameQ;
    private String gameName;
    private Integer giftId;
    private String giftName;
    private String giftType;
    private String giftProductId;
    private String giftProductName;
    private String giftProductImg;
    private String csgoType;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    private String giftGrade;
    private BigDecimal giftPrice;
    private String giftGradeG;
}
