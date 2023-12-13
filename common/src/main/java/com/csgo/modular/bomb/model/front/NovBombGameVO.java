package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class NovBombGameVO {

    @ApiModelProperty(value = "游戏id")
    private Integer id;

    @ApiModelProperty(value = "最低值")
    private BigDecimal lowPrice;

    @ApiModelProperty(value = "最高值")
    private BigDecimal highPrice;

}
