package com.csgo.web.response.blindbox;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

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
    private BigDecimal price;
    private BigDecimal probability;
    private int weight;
    private int outProbability;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private String productName;
}
