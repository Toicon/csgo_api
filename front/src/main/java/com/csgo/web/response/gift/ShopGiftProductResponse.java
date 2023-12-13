package com.csgo.web.response.gift;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/5/6
 */
@Setter
@Getter
public class ShopGiftProductResponse {
    private Integer id;
    private String type;
    private BigDecimal price;
    private String name;
    private String img;
    private Date createdAt;
    private Date updatedAt;
    private Integer giftId;
    private String withinProbability;
    private int outProbability;
    private String gameName;
    private String isdefault;
    private String content;
    private String grade;
    private String englishName;
    private String itemId;
    private String csgoType;
    private String csgoTypeName;
    private String exteriorName;
    private String imgUrl;
    private Integer pageNum;
    private Integer pageSize;
    private Integer userMessageId;
    private Integer total;
    private Integer sortId;
    private Integer luckyId;
    private Integer isRecommend;
    private Double probability;
    private Integer randomId;
    private int userPrizeId;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private int stock;

    /**
     * 许愿饰品
     */
    private boolean wishing;

}
