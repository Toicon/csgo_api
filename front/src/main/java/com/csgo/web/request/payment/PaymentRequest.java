package com.csgo.web.request.payment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Admin on 2021/7/13
 */
@Setter
@Getter
public class PaymentRequest {
    @NotNull(message = "用户Id不能为空")
    private Integer userId;

    @NotNull(message = "金额不能为空")
    private BigDecimal price;

    @NotBlank(message = "签名错误")
    private String token;

    private Boolean isApp;

    private String select;
}
