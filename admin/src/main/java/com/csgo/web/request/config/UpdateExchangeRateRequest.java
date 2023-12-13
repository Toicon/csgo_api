package com.csgo.web.request.config;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class UpdateExchangeRateRequest {

    private BigDecimal shopSpillPrice;
}
