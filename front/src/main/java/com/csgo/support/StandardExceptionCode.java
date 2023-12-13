package com.csgo.support;

/**
 * @author admin
 */
public final class StandardExceptionCode {

    //用户相关
    public static final int VERIFICATION_CODE_FAILURE = 1000;
    public static final int REGISTER_FAILURE = 1001;
    public static final int STEAM_LOGIN_FAILURE = 1002;
    public static final int UPDATE_USER_FAILURE = 1003;
    public static final int COMMISSION_COLLECTION_FAILURE = 1004;
    public static final int USER_MESSAGE_SELL_FAILURE = 1005;
    public static final int BALANCE_LACK = 1006;
    public static final int GIFT_PRODUCT_DRAW_PRODUCT_FAILURE = 1007;
    public static final int RECHARGE_FAILURE = 1014;
    public static final int USER_ERROR = 1021;
    public static final int ALI_APP_LOGIN_FAILURE = 1008;
    //第三方接口相关
    public static final int ZBK_QUERY_FAILURE = 2001;

    public static final int CLIENT_ERROR = 4000;

    //权限相关
    public static final int AUTH_FAILURE = 4030;

    //业务相关
    public static final int RED_ENVELOP_NOT_EXIST = 5001;
    public static final int LIMIT_AMOUNT_NO_ACHIEVE = 5002;
    public static final int RED_ENVELOP_RECEIPTED = 5003;
    public static final int RED_ENVELOP_IS_EMPTY = 5004;
    public static final int RED_ENVELOP_LOGIN_REQUIRED = 5005;
    public static final int DRAW_LIMIT = 5006;
    public static final int LOTTERY_DRAW_PRODUCT_NOT_EXISTS = 5020;
    public static final int LOTTERY_DRAW_FAILURE = 5021;
    public static final int LUCKY_PRODUCT_DRAW_NOT_EXISTS = 5030;
    public static final int LUCKY_PRODUCT_DRAW_LUCKY_VALUE_ILLEGAL = 5031;
    public static final int LUCKY_PRODUCT_DRAW_PRICE_ILLEGAL = 5032;
    public static final int LUCKY_PRODUCT_DRAW_PRODUCT_NOT_EXISTS = 5033;
    public static final int STEAM_NOT_EXISTS = 5034;
    public static final int STEAM_EXISTS = 5037;
    public static final int WITHDRAW_FAILURE = 5035;
    public static final int LUCKY_PRODUCT_DRAW_PRODUCT_FAILURE = 5036;
    public static final int BLIND_BOX_ROOM_CHALLENGE_FAILURE = 5041;
    public static final int BLIND_BOX_ROOM_JOIN_FAILURE = 5044;
    public static final int SHOP_STOCK_LACK = 5050;
    //饰品升级获取不到随机物品异常
    public static final int SYS_ERROR_LUCKY_RANDOM = 8000;
    //箱子最低物品价格大于等于首页开箱价格异常
    public static final int SYS_ERROR_HOME_OPEN = 8001;
    //扫雷玩法获取不到随机物品异常
    public static final int SYS_ERROR_MINE_OPEN = 8002;

    /**
     * 未实名认证
     */
    public static final int REAL_NAME_NOT_AUTH = 7000;

    public static final int SERVER_ERROR = 5000;

}
