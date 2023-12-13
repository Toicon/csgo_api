package com.csgo.web.response.prize;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPrizePlusResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userNameQ;
    private String gameName;
    private Integer giftId;
    private String giftType;
    private String giftProductId;
    private String giftProductName;
    private String giftProductImg;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    private String giftGrade;
    private BigDecimal giftPrice;
    private String giftGradeG;
    private String giftName;
}
