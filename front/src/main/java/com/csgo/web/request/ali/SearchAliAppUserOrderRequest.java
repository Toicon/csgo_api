package com.csgo.web.request.ali;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SearchAliAppUserOrderRequest extends PageRequest {
    //订单状态（ 0：待发货 1：已发货）
    @NotNull(message = "订单状态不能为空")
    private Integer orderStatus;
}
