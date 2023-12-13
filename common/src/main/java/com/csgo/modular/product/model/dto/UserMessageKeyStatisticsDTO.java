package com.csgo.modular.product.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class UserMessageKeyStatisticsDTO {

    private Integer productId;
    private String productName;
    private String productImg;

    @ApiModelProperty("数量")
    private Integer productNum;

}
