package com.csgo.domain.plus.gift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/7/14
 */
@Setter
@Getter
public class GiftProductDTO {

    private int id;

    private String name;

    private String img;

    private BigDecimal price;

    private BigDecimal zbtPrice;

    private Integer zbtStock;

    private String boxName;

    private String lucky;
}
