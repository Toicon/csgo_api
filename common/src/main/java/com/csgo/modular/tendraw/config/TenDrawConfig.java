package com.csgo.modular.tendraw.config;

import com.beust.jcommander.internal.Lists;
import com.csgo.modular.tendraw.support.TenDrawBall;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
public class TenDrawConfig {

    public static final Integer PLAY_MAX_TIMES = 10;
    public static final Integer PRODUCT_COUNT = 20;

    @Deprecated
    public static List<TenDrawBall> BALL_CONFIG = Lists.newArrayList();
    @Deprecated
    public static final Integer BALL_MAX_SIZE = 10;
    @Deprecated
    public static final Integer COLOR_INDEX_GOLD = 17;
    @Deprecated
    public static final Integer COLOR_INDEX_YELLOW = 10;



    static {
        BALL_CONFIG.add(new TenDrawBall(1, "白", "https://49skins.oss-cn-hangzhou.aliyuncs.com/front/image/ball/ball1.png", new BigDecimal("0.01")));
        BALL_CONFIG.add(new TenDrawBall(2, "蓝", "https://49skins.oss-cn-hangzhou.aliyuncs.com/front/image/ball/ball2.png", new BigDecimal("0.05")));
        BALL_CONFIG.add(new TenDrawBall(3, "绿", "https://49skins.oss-cn-hangzhou.aliyuncs.com/front/image/ball/ball3.png", new BigDecimal("0.1")));
        BALL_CONFIG.add(new TenDrawBall(4, "粉", "https://49skins.oss-cn-hangzhou.aliyuncs.com/front/image/ball/ball4.png", new BigDecimal("0.25")));
        BALL_CONFIG.add(new TenDrawBall(5, "紫", "https://49skins.oss-cn-hangzhou.aliyuncs.com/front/image/ball/ball5.png", new BigDecimal("0.5")));
        BALL_CONFIG.add(new TenDrawBall(6, "红", "https://49skins.oss-cn-hangzhou.aliyuncs.com/front/image/ball/ball6.png", new BigDecimal("0.75")));
        BALL_CONFIG.add(new TenDrawBall(7, "金", "https://49skins.oss-cn-hangzhou.aliyuncs.com/front/image/ball/ball7.png", new BigDecimal("1")));
    }

}
