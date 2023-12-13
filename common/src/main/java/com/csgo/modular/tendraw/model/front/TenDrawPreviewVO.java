package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Data
public class TenDrawPreviewVO {

    @ApiModelProperty(value = "唯一标识")
    private String uuid;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "重试金额")
    private BigDecimal retryPrice;

    @ApiModelProperty(value = "掉落概率")
    private Integer blueRate;

    @ApiModelProperty(value = "掉落概率")
    private Integer redRate;

    @ApiModelProperty(value = "掉落概率")
    private Integer yellowRate;

    @ApiModelProperty(value = "饰品预览")
    private List<TenDrawProductVO> productList;

}
