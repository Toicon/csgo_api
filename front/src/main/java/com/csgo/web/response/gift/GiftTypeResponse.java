package com.csgo.web.response.gift;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class GiftTypeResponse {
    private Integer id;
    private String img;
    private String name;

    private String type;

    private Boolean hidden;

    private Date ct;

    private Date ut;

    private Integer sort;
}