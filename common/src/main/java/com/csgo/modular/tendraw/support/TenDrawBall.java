package com.csgo.modular.tendraw.support;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class TenDrawBall {

    private Integer id;

    private String name;

    private String img;

    private BigDecimal weight;

    public TenDrawBall(Integer id, String name, String img, BigDecimal weight) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.weight = weight;
    }

}
