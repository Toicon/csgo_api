package com.csgo.web.response.anchor;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class BoxAnchorJackpotResponse {

    private BigDecimal balance;
    private BigDecimal spareBalance;

}
