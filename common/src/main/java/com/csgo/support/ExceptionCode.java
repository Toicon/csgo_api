package com.csgo.support;

/**
 * @author admin
 */
public enum ExceptionCode {

    SEND_MSG_ERROR(0006, "发送短信失败，请联系管理员！"),
    UNAUTHORIZED(4001, "登录超时，请重新登录"),
    OPERATION_FORBIDDEN(4003, "无操作权限"),
    RESOURCE_NOT_FOUND(4004, "对应的资源找不到"),
    INTERNAL_SERVER_ERROR(5000, "系统异常"),
    BIND_BOX_EXISTS(5001, "盲盒已存在"),
    WITHDRAW_ERROR(5003, "取回功能不可用"),
    SHOP_EXISTS(5004, "当前商城类型下已存在道具"),
    RATE_NOT_EXISTS(5005, "汇率为空，请稍后再试"),
    PAY_FAILURE(5006, "支付失败，请重新再试"),
    RECHARGE_CONFIG_ERROR(5007, "畅想支付，充值配置错误"),
    NO_BINDING_ERROR(5008, "请先绑定steam链接在进行充值"),
    CDK_NOT_FOUND(5009, "CDK不存在"),
    CDK_ERROR(5010, "CDK错误，请联系客服！"),
    CDK_IS_USED(5011, "CDK已使用！"),
    CDK_REPEAT(5012, "领取中，请勿重复领取。"),
    STEAM_PHONE_REPEAT(5013, "短信验证码发送中，请勿重复发送。"),
    MSG_CODE_ERROR(5014, "短信验证码错误。"),
    STEAM_DAY_ONE(5015, "STEAM链接每日仅限修改一次，请联系客服！"),
    REPEAT_OPERATION(5016, "操作进行中，请勿重复操作！"),
    MEMBERSHIP_RECEIVE_ERROR(5023, "正在领取，请勿重复点击！"),
    MEMBERSHIP_TASK_ERROR(5024, "任务奖励不可领取！"),
    MEMBERSHIP_BOX_CONFIG_ERROR(5025, "当前等级已配置！"),
    FIRST_RECHARGE_EXIST(5026, "已购买首充，无法重复购买"),
    AMMUNITION_NOT_ENOUGH(7001, "弹药不足！"),
    USED_COIN_LOTTERY(7002, "弹药不足,今日已使用金币抽奖！"),
    //
    ;


    private final int code;
    private final String message;

    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
