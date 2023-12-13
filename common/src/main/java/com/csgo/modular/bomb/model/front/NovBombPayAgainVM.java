package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode
public class NovBombPayAgainVM {

    @ApiModelProperty(value = "支付金额")
    @NotNull(message = "支付金额不能为空")
    private BigDecimal payPrice;

}
