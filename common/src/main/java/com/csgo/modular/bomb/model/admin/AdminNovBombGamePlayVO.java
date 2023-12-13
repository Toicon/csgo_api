package com.csgo.modular.bomb.model.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Data
public class AdminNovBombGamePlayVO {

    private Integer id;

    private Integer gameId;

    private Integer userId;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal price;

    @ApiModelProperty(value = "奖励饰品ID")
    private Integer rewardProductId;

    @ApiModelProperty(value = "奖励饰品名称")
    private String rewardProductName;

    @ApiModelProperty(value = "奖励商品图片")
    private String rewardProductImg;

    @ApiModelProperty(value = "奖励饰品价格")
    private BigDecimal rewardProductPrice;

    @ApiModelProperty(value = "颜色")
    private Integer color;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

}
