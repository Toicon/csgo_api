package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2021/5/25
 */
@Setter
@Getter
public class OfferInfoDTO {
    //steam报价id，拼接到steam接受报价的地址 https://steamcommunity.com/tradeoffer/{tradeOfferId}
    private String tradeOfferId;
    //ZBT 平台的offerId， 同样只有3的状态下可以返回。可以使用查询报价状态接口 /open/offer/v1/status 获取更多信息。例如报价中的双方steam账号，以及报价中的饰品信息等
    private String transferId;
}
