package com.csgo.web.response.roll;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/6/25
 */
@Setter
@Getter
public class RollGiftResponse {

    private Integer id;
    private Integer rollGiftId;
    private Integer giftProductId;
    private BigDecimal price;
    private String productname;
    private String img;
    private String grade;
    private Integer rollId;
}
