package com.csgo.modular.bomb.support;

import com.csgo.modular.bomb.model.NovBombSettingVO;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author admin
 */
@Data
public class NovBombPriceHelper {

    private final Integer num;
    private final BigDecimal basicPrice;
    private List<BigDecimal> priceList;

    private NovBombSettingVO setting;

    public NovBombPriceHelper(NovBombSettingVO setting, Integer num, BigDecimal productPriceParams, BigDecimal basic) {
        Assert.notNull(num, "数量不能为空");
        Assert.notNull(basic, "权重不能为空");
        this.num = num;
        this.basicPrice = basic;
        this.setting = setting;
        handleInit(num, productPriceParams, basic);
    }

    private void handleInit(Integer num, BigDecimal productPriceParams, BigDecimal basicPrice) {
        List<BigDecimal> priceList = Lists.newArrayList();

        for (int i = 1; i <= num; i++) {
            BigDecimal price = basicPrice.multiply(setting.getExp())
                    .multiply(BigDecimal.valueOf(Math.exp(productPriceParams.doubleValue() * i)))
                    .setScale(5, RoundingMode.UP);
            priceList.add(price);
        }
        this.priceList = priceList;
    }

    public List<BigDecimal> getPriceList() {
        return priceList;
    }

//    public static void main(String[] args) {
//        NovBombSettingVO setting = new NovBombSettingVO();
//        setting.setExp(new BigDecimal("0.15"));
//
//        NovBombPriceHelper calc = new NovBombPriceHelper(setting, 5, BigDecimal.valueOf(0.5), BigDecimal.valueOf(100));
//        System.err.println(calc.getPriceList());
//    }

}
