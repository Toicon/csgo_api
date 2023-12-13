package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by admin on 2021/5/25
 */
@Setter
@Getter
public class ZBTOrderData {
    private String orderId;

    private BigDecimal price;

    private String productId;

    private String receiveSteamId;

    //订单状态值 状态0=待付款 1=待处理 2=处理中 3=待对方处理 10=已完成 11=已取消
    private Integer status;

    private String statusName;

    private String failedDesc;

    private OfferInfoDTO offerInfoDTO;
}
