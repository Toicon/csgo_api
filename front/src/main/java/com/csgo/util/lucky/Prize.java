package com.csgo.util.lucky;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "奖品实体", description = "奖品实体")
@Builder
public class Prize implements Serializable {

    @ApiModelProperty("奖品id")
    private int id;

    @ApiModelProperty("奖品名称")
    private String prize_name;

    @ApiModelProperty("奖品图片")
    private String prize_img;

    @ApiModelProperty("奖品（剩余）数量")
    private BigDecimal prize_amount;

    @ApiModelProperty("奖品权重")
    private double prize_weight;

    @ApiModelProperty("奖品Id")
    private Integer productId;

    @Override
    public String toString() {
        return "Prize{" +
                "id=" + id +
                ", prize_name='" + prize_name + '\'' +
                ", prize_img='" + prize_img + '\'' +
                ", prize_amount=" + prize_amount +
                ", prize_weight=" + prize_weight +
                '}';
    }
}
