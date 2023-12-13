package com.csgo.web.request.code;

import com.csgo.domain.plus.code.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class AddActivationCodeView {

    private Integer giftProductId;

    private String productName;

    private ProductType productType;

    private BigDecimal price;

    private int num;
    /**
     * 目标账号
     */
    private String targetUserName;

}
