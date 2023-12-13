package com.csgo.web.response.blindbox;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class BlindBoxResponse {
    private Integer id;
    private String boxImg;
    private BigDecimal price;
    private String name;
    private String img;
    private Integer grade;
    private Integer typeId;
    private Integer type;
    private Integer sortId;
    private Date addTime;
    private Date updateTime;
    private String typeName;
    private int productCount;
}