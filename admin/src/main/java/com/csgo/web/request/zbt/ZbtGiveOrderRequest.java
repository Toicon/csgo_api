package com.csgo.web.request.zbt;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Admin on 2021/7/19
 */
@Setter
@Getter
public class ZbtGiveOrderRequest {
    @NotBlank(message = "steam链接不能为空")
    private String steamUrl;

    @NotBlank(message = "道具不能为空")
    private String giftProductName;

    @NotBlank(message = "道具不能为空")
    private String itemId;

    @NotNull(message = "道具价格不能为空")
    private BigDecimal autoDeliverPrice;

    @NotNull(message = "道具价格不能为空")
    private BigDecimal manualDeliverPrice;

    @NotBlank(message = "发货方式不能为空")
    private String delivery;
}
