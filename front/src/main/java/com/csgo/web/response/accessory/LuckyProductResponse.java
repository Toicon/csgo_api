package com.csgo.web.response.accessory;

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
public class LuckyProductResponse {

    private Integer id;
    private Integer giftProductId;
    private Integer sortId;
    private String imgUrl;
    private BigDecimal price;
    private Integer isRecommend;
    private Integer typeId;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private String productName;
    private String exteriorName;
    private String englishName;
}
