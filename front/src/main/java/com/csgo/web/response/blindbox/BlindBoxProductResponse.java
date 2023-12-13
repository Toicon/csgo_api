package com.csgo.web.response.blindbox;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class BlindBoxProductResponse {
    private Integer id;
    private Integer blindBoxId;
    private Integer giftProductId;
    private String imgUrl;
    private Date addTime;
    private Date updateTime;
    private BigDecimal price;
    private Double probability;
    private String productName;
    private int outProbability;
}
