package com.csgo.domain.plus.accessory;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyProductDTO {
    private int id;
    private String imgUrl;
    private Integer giftProductId;
    private BigDecimal price;
    private String productName;
    private String englishName;
    private String exteriorName;
}
