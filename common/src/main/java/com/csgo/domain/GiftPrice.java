package com.csgo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GiftPrice {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "礼包的id", required = true)
    private Integer giftId;

    @ApiModelProperty(value = "礼包数量", required = true)
    private Integer num;

    @ApiModelProperty(value = "礼包价格", required = true)
    private BigDecimal price;

    private Integer total;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}