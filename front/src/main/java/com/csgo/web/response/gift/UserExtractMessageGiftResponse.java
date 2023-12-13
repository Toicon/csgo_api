package com.csgo.web.response.gift;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UserExtractMessageGiftResponse {
    @ApiModelProperty(value = "礼包商品名称", required = true)
    private String giftProductName;
    @ApiModelProperty(value = "图片信息", required = true)
    private String img;
    @ApiModelProperty(value = "提取价格", required = true)
    private BigDecimal money;
    @ApiModelProperty(value = "提取时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ut;
    @ApiModelProperty(value = "提取结果 （2：已提取 3:提取中）", required = true)
    private Integer state;
}