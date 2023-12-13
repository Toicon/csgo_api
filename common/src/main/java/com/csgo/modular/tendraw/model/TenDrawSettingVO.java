package com.csgo.modular.tendraw.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class TenDrawSettingVO {

    public Integer ballMaxSize;
    public Integer colorIndexGold;
    public Integer colorIndexYellow;

    private BigDecimal basicWeight;
    private BigDecimal payPriceMaxWeight;
    private BigDecimal payPriceRate;
    private BigDecimal retryPayPriceRate;

    private Double exp;

}
