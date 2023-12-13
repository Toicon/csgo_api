package com.csgo.support.ZBT;

import java.math.BigDecimal;

public class PriceInfo {

    private Integer appId;

    private String itemId;

    private String marketHashName;

    //自动发货最低价格
    private BigDecimal autoDeliverPrice;

    //自动发货在售数量
    private Integer autoDeliverQuantity;
    //人工发货最低价格
    private BigDecimal manualDeliverPrice;
    //人工发货在售数量
    private Integer manualQuantity;

    //在售价格
    private BigDecimal price;
    //在售数量
    private Integer quantity;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getMarketHashName() {
        return marketHashName;
    }

    public void setMarketHashName(String marketHashName) {
        this.marketHashName = marketHashName;
    }

    public BigDecimal getAutoDeliverPrice() {
        return autoDeliverPrice;
    }

    public void setAutoDeliverPrice(BigDecimal autoDeliverPrice) {
        this.autoDeliverPrice = autoDeliverPrice;
    }

    public Integer getAutoDeliverQuantity() {
        return autoDeliverQuantity;
    }

    public void setAutoDeliverQuantity(Integer autoDeliverQuantity) {
        this.autoDeliverQuantity = autoDeliverQuantity;
    }

    public BigDecimal getManualDeliverPrice() {
        return manualDeliverPrice;
    }

    public void setManualDeliverPrice(BigDecimal manualDeliverPrice) {
        this.manualDeliverPrice = manualDeliverPrice;
    }

    public Integer getManualQuantity() {
        return manualQuantity;
    }

    public void setManualQuantity(Integer manualQuantity) {
        this.manualQuantity = manualQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PriceInfo{" +
                "autoDeliverPrice=" + autoDeliverPrice +
                ", autoDeliverQuantity=" + autoDeliverQuantity +
                ", manualDeliverPrice=" + manualDeliverPrice +
                ", manualQuantity=" + manualQuantity +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
