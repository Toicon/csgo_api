package com.csgo.domain.plus.withdraw;

import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.modular.product.util.MoneyRateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class WithdrawPropRelateDTO {

    private Integer id;
    public String giftProductName;
    public String giftProductImg;
    private BigDecimal price;
    private BigDecimal zbtPrice;
    private BigDecimal rechargeAmount;
    private String steamUrl;
    private String name;
    private String img;
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date createAt; //加入时间
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ut; //报价时间
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct; //提取时间

    /**
     * 获取美元价格
     */
    public BigDecimal getUsdPrice() {
        return MoneyRateUtil.getUsdPrice(zbtPrice);
    }

}
