package com.csgo.modular.bomb.model.front;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Data
public class NovBombPreviewVO {

    @ApiModelProperty(value = "唯一标识")
    private String uuid;

    @ApiModelProperty(value = "场次ID")
    private Integer configId;

    @ApiModelProperty(value = "金额")
    private BigDecimal price;

    @ApiModelProperty(value = "最低值")
    private BigDecimal lowPrice;

    @ApiModelProperty(value = "最高值")
    private BigDecimal highPrice;

    @ApiModelProperty(value = "最大次数")
    private Integer maxTimes;

    @ApiModelProperty(value = "饰品列表")
    private List<NovBombProductVO> productList;

}
