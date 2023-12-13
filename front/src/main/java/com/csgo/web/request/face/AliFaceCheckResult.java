package com.csgo.web.request.face;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 人脸识别成功返回信息
 */
@Data
public class AliFaceCheckResult {
    /**
     * 充值金额
     */
    @ApiModelProperty(notes = "充值金额")
    private BigDecimal price;
    /**
     * 公司名称
     */
    @ApiModelProperty(notes = "公司名称")
    private String companyName;
    /**
     * 商品名称
     */
    @ApiModelProperty(notes = "商品名称")
    private String productName;

    /**
     * 订单编号
     */
    @ApiModelProperty(notes = "订单编号")
    private String orderNo;

}
