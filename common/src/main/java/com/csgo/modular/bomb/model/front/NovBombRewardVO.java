package com.csgo.modular.bomb.model.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class NovBombRewardVO {

    @ApiModelProperty(value = "奖励商品id")
    private Integer rewardProductId;

    @ApiModelProperty(value = "奖励商品图")
    private String rewardProductImg;

    @ApiModelProperty(value = "奖励商品名称")
    private String rewardProductName;

    @ApiModelProperty(value = "奖励商品金额")
    private BigDecimal rewardProductPrice;

    @ApiModelProperty(value = "商品下标")
    private Integer rewardProductIndex;

    @ApiModelProperty(value = "饰品标签")
    private String rewardProductExteriorName;

    @ApiModelProperty(value = "领取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date rewardDate;

}
