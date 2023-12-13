package com.csgo.web.response.box;

import com.csgo.web.response.gift.GiftResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class TreasureBoxResponse {

    private int id;
    private String name;
    private String img;
    private String halationImg;
    private String bgImg;
    private String cb;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    private String ub;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ut;

    private List<GiftResponse> giftList;
}
