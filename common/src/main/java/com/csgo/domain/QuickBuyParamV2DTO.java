package com.csgo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class QuickBuyParamV2DTO {

    @ApiModelProperty(value = "购买可以接受的最高价格", required = true)
    private BigDecimal maxPrice;

    @ApiModelProperty(value = "饰品id", required = true)
    private String itemId;

    @ApiModelProperty(value = "可选参数，发货模式，1=人工，2自动", required = true)
    private Integer delivery;

    @ApiModelProperty(value = "商户订单号", required = true)
    private String outTradeNo;

    @ApiModelProperty(value = "收货的steam交易链接", required = true)
    private String tradeUrl;


    public Integer getDelivery() {
        return delivery;
    }

    public void setDelivery(Integer delivery) {
        this.delivery = delivery;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTradeUrl() {
        return tradeUrl;
    }

    public void setTradeUrl(String tradeUrl) {
        this.tradeUrl = tradeUrl;
    }
}
