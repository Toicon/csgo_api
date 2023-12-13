package com.csgo.domain.plus.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class UserCurrencyExchangeDTO {

    //兑换金额(银币)
    @ApiModelProperty(notes = "兑换金额(银币)")
    private BigDecimal diamondAmount;
    //赠送金额(银币)
    @ApiModelProperty(notes = "赠送金额(银币)")
    private BigDecimal giveAmount;
    //创建时间
    @ApiModelProperty(notes = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;
}
