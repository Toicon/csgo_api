package com.csgo.framework.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author admin
 */
@Slf4j
public class LotteryUtil {

    public static int lottery(List<Double> ratesList) {
        if (ratesList == null || ratesList.isEmpty()) {
            log.error("lottery ratesList isEmpty");
            return -1;
        }

        // 计算总概率，这样可以保证不一定总概率是1
        Double sumRate = 0d;
        for (Double rate : ratesList) {
            sumRate += rate;
        }

        if (sumRate <= 0d) {
            log.error("lottery sumRate:{} <=0", sumRate);
            return -1;
        }

        // 计算每个物品在总概率的基础下的概率情况
        double rate = 0d;
        List<Double> sortRateList = new ArrayList<>(ratesList.size());
        for (double prob : ratesList) {
            rate += prob;
            sortRateList.add(rate / sumRate);
        }

        // 随机生成一个随机数（0-1之间），并排序
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        double random = threadLocalRandom.nextDouble(0, 1);

        sortRateList.add(random);
        Collections.sort(sortRateList);

        return sortRateList.indexOf(random);
    }

}
