package com.csgo.web.response.code;

import java.math.BigDecimal;
import com.csgo.domain.plus.code.ProductType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class ActivationCodeResult {
    @ApiModelProperty(notes = "领取奖品名称", example = "AK47")
    private String productName;
    @ApiModelProperty(notes = "领取奖品图片路径", example = "www.baidu.com")
    private String productImg;
    @ApiModelProperty(notes = "领取奖品等级", example = "#eb4b4b")
    private String grade;
    @ApiModelProperty(notes = "领取奖品价值", example = "15.21")
    private BigDecimal cost;
    @ApiModelProperty(notes = "奖品类型", example = "VB,PRODUCT")
    private ProductType productType;
    @ApiModelProperty(notes = "物品ID", example = "15.21")
    private Integer userMessageId;
}
