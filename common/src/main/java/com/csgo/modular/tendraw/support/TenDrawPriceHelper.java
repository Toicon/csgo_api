package com.csgo.modular.tendraw.support;

import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.modular.tendraw.model.TenDrawSettingVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
@Data
public class TenDrawPriceHelper {

//    private static final BigDecimal BASIC_WEIGHT = new BigDecimal("0.05");
//    private static final BigDecimal PAY_PRICE_MAX_WEIGHT = new BigDecimal("9500");
//    private static final BigDecimal PAY_PRICE_RATE = new BigDecimal("0.9");
//    private static final BigDecimal RETRY_PAY_PRICE_RATE = new BigDecimal("0.15");

    private final Integer num;
    private final BigDecimal ballSumValue;

    private List<BigDecimal> priceList;
    private List<BigDecimal> showWeightList;
    private List<BigDecimal> priceWeightList;

    private Map<Integer, Integer> colorMap;

    private BigDecimal payPrice;
    private BigDecimal retryPrice;

    private Integer blueRate;
    private Integer redRate;
    private Integer yellowRate;

    private TenDrawSettingVO setting;

    public TenDrawPriceHelper(TenDrawSettingVO setting, Integer num, BigDecimal ballSumValue) {
        Assert.notNull(num, "数量不能为空");
        Assert.notNull(ballSumValue, "权重不能为空");
        this.num = num;
        this.ballSumValue = ballSumValue;
        this.setting = setting;
        handleInit(num, ballSumValue);
    }

    private void handleInit(Integer num, BigDecimal ballSumValue) {
        List<BigDecimal> priceList = Lists.newArrayList();
        BigDecimal sumWeight = ballSumValue.add(setting.getBasicWeight());
        for (int i = 1; i <= num; i++) {
            BigDecimal price = sumWeight.multiply(BigDecimal.valueOf(Math.exp(setting.getExp() * i))).setScale(5, RoundingMode.UP);
            priceList.add(price);
        }
        this.priceList = priceList;

        BigDecimal priceSum = priceList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        List<BigDecimal> showWeightList = Lists.newArrayList();
        for (int i = 0; i < priceList.size(); i++) {
            BigDecimal price = priceList.get(i);
            BigDecimal rate = priceSum.divide(price, 2, RoundingMode.UP);
            showWeightList.add(rate);
        }
        this.showWeightList = showWeightList;

        List<BigDecimal> priceWeightList = Lists.newArrayList();
        BigDecimal showWeightSum = showWeightList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        for (int i = 0; i < showWeightList.size(); i++) {
            BigDecimal showWeight = showWeightList.get(i);
            BigDecimal priceRate = showWeight.divide(showWeightSum, 5, RoundingMode.UP).multiply(new BigDecimal("10000"));
            priceWeightList.add(priceRate);
        }
        this.priceWeightList = priceWeightList;

        BigDecimal price = calcPrice(priceList, priceWeightList);
        this.payPrice = price.multiply(setting.getPayPriceRate()).setScale(2, RoundingMode.UP);
        this.retryPrice = price.multiply(setting.getRetryPayPriceRate()).setScale(2, RoundingMode.UP);

        BigDecimal priceWeightSum = priceWeightList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal yellowSum = BigDecimal.ZERO;
        BigDecimal redSum = BigDecimal.ZERO;


        Map<Integer, Integer> colorMap = Maps.newHashMap();
        for (int i = priceWeightList.size() - 1; i > 0; i--) {
            int index = i + 1;
            BigDecimal priceWeight = priceWeightList.get(i);
            if (index >= setting.getColorIndexGold()) {
                colorMap.put(i, 2);
                yellowSum = yellowSum.add(priceWeight);
            } else if (index >= setting.getColorIndexYellow()) {
                redSum = redSum.add(priceWeight);
                colorMap.put(i, 1);
            } else {
                break;
            }
        }
        this.colorMap = colorMap;

        this.yellowRate = yellowSum.divide(priceWeightSum, 2, RoundingMode.UP).multiply(BigDecimal.valueOf(100)).intValue();
        this.redRate = redSum.divide(priceWeightSum, 2, RoundingMode.UP).multiply(BigDecimal.valueOf(100)).intValue();
        this.blueRate = 100 - yellowRate - redRate;
    }

    private BigDecimal calcPrice(List<BigDecimal> priceList, List<BigDecimal> priceWeightList) {
        BigDecimal sum = BigDecimal.ZERO;

        BigDecimal payPrice = null;
        for (int i = 0; i < priceWeightList.size(); i++) {
            BigDecimal priceWeight = priceWeightList.get(i);
            sum = sum.add(priceWeight);
            if (BigDecimalUtil.greaterEqual(sum, setting.getPayPriceMaxWeight())) {
                payPrice = priceList.get(i);
                break;
            }
        }
        if (payPrice == null) {
            payPrice = priceList.get(priceList.size() - 1);
        }
        return payPrice;
    }

    public List<BigDecimal> getPriceList() {
        return priceList;
    }

//    public static void main(String[] args) {
//        TenDrawPriceHelper calc = new TenDrawPriceHelper(20, BigDecimal.valueOf(3.5));
//        System.err.println(calc.getPriceList());
//        System.err.println(calc.getShowWeightList());
//        System.err.println(calc.getPriceWeightList());
//        System.err.println(calc.getPayPrice());
//        System.err.println(calc.getRetryPrice());
//        System.err.println("----------------");
//        System.err.println(calc.getBlueRate());
//        System.err.println(calc.getRedRate());
//        System.err.println(calc.getYellowRate());
//    }

}
