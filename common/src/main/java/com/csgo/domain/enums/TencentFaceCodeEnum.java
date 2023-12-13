package com.csgo.domain.enums;

/**
 * 腾讯人脸识别返回状态码
 */
public enum TencentFaceCodeEnum {
    YES("0", "响应成功"),
    IDENTITY_ERROR("66660015", "姓名或身份证号格式不正确");

    TencentFaceCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;

    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
