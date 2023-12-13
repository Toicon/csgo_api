package com.csgo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 掉落概率
 */
public class LotteryUtil {

    /**
     * 抽奖
     *
     * @param giftProbList 所有抽奖奖项的概率集合（一定要按照奖项集合的顺序）
     * @return 抽奖结果的奖项集合列表索引
     */
    public static int draw(List<Double> giftProbList) {
        List<Double> sortRateList = new ArrayList<>();
        // 计算概率总和
        Double sumRate = 0D;
        for (Double prob : giftProbList) {
            sumRate += prob;
        }
        if (sumRate != 0) {
            double rate = 0D; // 概率所占比例
            for (Double prob : giftProbList) {
                rate += prob;
                // 构建一个比例区段组成的集合(避免概率和不为1)(概率/概率和 = 概率占比)
                sortRateList.add(rate / sumRate);
            }
            // 随机生成一个随机数（0-1之间），并排序
            ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
            double random = threadLocalRandom.nextDouble(0, 1);
            sortRateList.add(random);
            Collections.sort(sortRateList);
            // 返回该随机数在比例集合中的索引（随机数在概率比区间位置索引及为随机中奖）
            return sortRateList.indexOf(random);
        }
        return -1;
    }
}
