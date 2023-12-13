package com.csgo.constants;

import com.csgo.framework.exception.BizCode;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
public enum CommonBizCode implements BizCode {

    // COMMON
    COMMON_SYSTEM_ERROR(10009999, "系统错误"),
    COMMON_BUSY(10009998, "操作过于频繁，请稍后再试"),
    COMMON_SYSTEM_CONFIG_ERROR(10009997, "系统配置错误，请联系客服"),
    COMMON_TYPE_NOT_SUPPORT(10009996, "不支持的类型"),
    COMMON_CAN_NOT_REPEAT(10009995, "请勿重复操作"),
    COMMON_STATUS_INCORRECT(10009994, "状态不正确"),
    COMMON_PARAM_ILLEGAL(10009400, "请求参数错误"),
    COMMON_DATA_NOT_FOUND(10009410, "数据不存在"),
    // Game
    GAME_NOT_FOUND(10008000, "游戏不存在"),
    GAME_FINISH(10008001, "游戏已结束"),
    GAME_UN_FINISH(10008001, "游戏未结束"),
    // 饰品
    PRODUCT_NOT_FOUND(10009400, "饰品不存在"),
    PRODUCT_SELL_FAIL(10001005, "商品出售失败"),
    // 用户
    USER_EMAIL_NOT_EXIST(100200, "请完善您的邮箱信息"),
    USER_EMAIL_BLANK(100201, "邮箱不能为空"),
    USER_EMAIL_ILLEGAL(100202, "邮箱格式错误"),
    USER_BALANCE_LACK(10001006, "账户V币余额不足，请充值后再试"),
    USER_DIAMOND_LACK(10001006, "账户银币不足！"),
    USER_NOT_FOUND(10004000, "用户不存在"),
    USER_LOGIN_FAIL(10004030, "用户名或密码错误"),
    USER_NAME_BLANK(10004030, "用户名不能为空"),
    USER_PASSWORD_BLANK(10004030, "密码不能为空"),
    USER_MOBILE_BLANK(10004030, "手机号不能为空"),
    USER_MOBILE_ILLEGAL(10004030, "手机号不合法"),
    USER_REGISTER_MOBILE_ALREADY(10004030, "该手机号已经注册过"),
    USER_REGISTER_ERROR(10004030, "注册失败"),
    USER_SMS_CODE_BLANK(10004030, "验证码不能为空"),
    USER_SMS_CODE_ERROR(10004030, "无效的短信验证码或短信验证码已失效"),
    USER_IMAGE_CODE_ERROR(10004030, "图形验证码不正确"),
    USER_SMS_CODE_SEND_ERROR(10004030, "短信发生失败"),
    USER_ACCOUNT_FROZEN(10004030, "当前账户已冻结，请联系客服"),
    USER_ACCOUNT_DELETED(10004030, "账号已经被删除"),
    USER_OLD_PASSWORD_BLANK(10004030, "旧密码不能为空"),
    USER_OLD_PASSWORD_ERROR(10004030, "旧密码错误"),
    USER_PROMOTION_CODE_ERROR(10004030, "推广码无效，请确认后再试"),
    USER_PROMOTION_CODE_FILL_TIMEOUT(10004030, "已超过填写时间！"),
    USER_USERNAME_ILLEGAL(10004030, "昵称命名不合法"),
    USER_USERNAME_EXIST(10004030, "昵称已被使用，请重新填写！"),
    USER_BIND_MOBILE_EXIST(10004030, "该手机号已被绑定"),
    USER_REAL_NAME_NOT_AUTH(10007000, "请先完成实名认证"),
    // 开箱
    SYS_ERROR_LUCKY_RANDOM(10008000, "发生未知系统异常,请联系客服。"),
    SYS_ERROR_HOME_OPEN(10008001, "发生未知系统异常,请联系客服。"),
    SYS_ERROR_MINE_OPEN(10008002, "发生未知系统异常,请联系客服。"),
    DRAW_LIMIT(10005006, "充值任意金额开通权限"),
    // 钥匙宝箱
    KEY_BOX_KEY_LEAK(10005006, "碎片数量不足"),
    KEY_BOX_KEY_CANNOT_WITHDRAW(10005006, "钥匙饰品无法提取"),
    // 验证
    CODE_VERIFY_PARAM_ILLEGAL(10003000, "验证码参数错误"),
    CODE_VERIFY_ERROR(10003001, "验证码错误"),
    CODE_IMAGE_BLANK(10001001, "图形验证码不能为空"),
    CODE_IMAGE_ERROR(10001001, "图形验证码不正确"),
    CODE_IMAGE_EXPIRE(10001001, "图形验证码不正确或者已经过期，请重新获取"),
    // 发货相关
    WITHDRAW_DISABLED(10005000, "取回功能已禁用"),
    WITHDRAW_FAIL(10005001, "饰品提取失败"),
    STEAM_URL_ILLEGAL(10006001, "STEAM链接不合法"),
    STEAM_URL_NOT_EXISTS(10006002, "请先绑定steam"),
    // 十连
    TEN_DRAW_UN_PLAY(21001200, "未进行开箱，无法放入背包"),
    TEN_DRAW_DATA_INVALID(21001201, "信息已失效，请重新生成奖励"),
    // 拆弹
    NOV_BOMB_DATA_INVALID(22001201, "信息已失效，请刷新页面"),
    NOV_BOMB_TIMES_LIMIT(22001202, "超过增加难度次数，无法继续增加难度"),
    // 心愿单
    GIFT_WISH_UN_COMPLETE(23001000, "心愿未达成"),
    GIFT_WISH_EXIST(23001001, "心愿已存在"),
    // 商店
    SHOP_STOCK_LACK(24001000, "商品库存不足"),
    // 红包
    RED_ENVELOP_NOT_EXIST(10005001, "红包不存在"),
    RED_ENVELOP_NOT_START(10005001, "当前活动红包未开始"),
    RED_ENVELOP_NOT_END(10005001, "当前活动红包已结束"),
    RED_ENVELOP_LEAK(10005001, "红包剩余数量不足，请稍后再试"),
    RED_ENVELOP_RECEIPTED(10005001, "您已领取过红包了"),
    RED_LIMIT_AMOUNT_NO_ACHIEVE(10005001, "参与失败，再充值{0}V币参与"),

    ;

    private final Integer code;
    private final String message;
    private final String i18n;

    CommonBizCode(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.i18n = this.name();
    }

    CommonBizCode(Integer code, String message, String i18n) {
        this.code = code;
        this.message = message;
        this.i18n = i18n;
    }

}
