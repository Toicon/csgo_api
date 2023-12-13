package com.csgo.modular.system.model.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class SystemJackpotVO {

    private BigDecimal balance;
    private BigDecimal spareBalance;

}
