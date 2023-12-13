package com.csgo.web.response.zbt;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/7/19
 */
@Setter
@Getter
public class ZbtGiveOrderResponse {

    private String name;

    private String itemId;

    private BigDecimal price;

    private String type;

    private String status;

    private String steamUrl;

    private String nickName;

    private String avatar;

    private String cb;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
