package com.csgo.web.response.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class GiftProductResponse {
    private Integer id;
    private String type;
    private BigDecimal price;
    private BigDecimal originAmount;
    private String name;
    private String img;
    private BigDecimal zbtPrice;
    private Integer zbtStock;
    private String boxName;
    private String blindName;
    private String lucky;
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
    private String exteriorName;
    private String csgoTypeName;
}
