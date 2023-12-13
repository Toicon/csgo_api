package com.csgo.service.fish;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyGiftProduct {
    private Integer id;
    private String type;
    private BigDecimal price;
    private String name;
    private String img;
    private Date createdAt;
    private Date updatedAt;
    private Integer giftId;
    private String gameName;
    private String isdefault;
    private String content;
    private String grade;
    private String englishName;
    private String itemId;
    private String csgoType;
    private String exteriorName;
    private BigDecimal probability;
    private int weight;

    private int outProbability;
    private int userMessageId;
    private int userPrizeId;

    private BigDecimal rebateAmount;
}
