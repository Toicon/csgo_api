package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class UserBalanceUpdateRequest {

    @NotNull(message = "用户不能为空")
    private Integer userId;

    @NotNull(message = "类型不能为空")
    private Integer type;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "最小金额不能小于0.01")
    private BigDecimal amount;

}
