package com.csgo.domain.plus.roll;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/5/19
 */
@Setter
@Getter
public class RollUserDTO {

    private Integer userId;

    private String userName;

    private String name;

    private Integer flag;

    private BigDecimal rechargeAmount;

}
