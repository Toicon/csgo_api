package com.csgo.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/6/25
 */
@Setter
@Getter
public class RollGiftResponse {

    private Integer id;
    private Integer giftProductId;
    private BigDecimal price;
    private String productname;
    private String img;
    private String grade;
    private Integer rollId;
}
