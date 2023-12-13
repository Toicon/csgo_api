package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class NovBombConfigVO {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "金额")
    private BigDecimal price;

    @ApiModelProperty(value = "最低值")
    private BigDecimal lowPrice;

    @ApiModelProperty(value = "最高值")
    private BigDecimal highPrice;

}
