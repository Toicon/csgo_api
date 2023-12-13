package com.csgo.web.request.recharge;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Created by Admin on 2021/7/13
 */
@Setter
@Getter
public class RechargeRequest {

    private int channelId;
    private int priceItemId;
    @NotBlank(message = "签名错误")
    private String token;
    private Boolean isApp;
}
