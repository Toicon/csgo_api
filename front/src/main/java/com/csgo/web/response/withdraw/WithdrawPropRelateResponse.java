package com.csgo.web.response.withdraw;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/5/25
 */
@Setter
@Getter
public class WithdrawPropRelateResponse {
    private Integer id;
    public String giftProductName;
    public String giftProductImg;
    private BigDecimal price;
    private String steamUrl;
    private String name;
    private String img;
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date createAt; //加入时间
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ut; //报价时间
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct; //提取时间
}
