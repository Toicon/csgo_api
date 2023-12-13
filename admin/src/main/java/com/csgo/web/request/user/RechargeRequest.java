package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class RechargeRequest {

    private int userId;
    @NotNull
    private BigDecimal price;
    @NotNull
    private String style;
}
