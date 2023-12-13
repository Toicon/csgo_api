package com.csgo.framework.sensitive;

/**
 * @author admin
 */
public enum SensitiveTypeEnum {

    /**
     * 自定义
     */
    CUSTOMER,

    /**
     * 用户名，张*化，谢*
     */
    CHINESE_NAME,

    /**
     * 地址，福建******
     */
    ADDRESS,

    /**
     * 身份证号，350325********2333
     */
    ID_CARD,

    /**
     * 座机号，****9999
     */
    FIXED_PHONE,

    /**
     * 银行卡，621232************3437
     */
    BANK_CARD,

    /**
     * 手机号, 183****1279
     */
    MOBILE_PHONE,

    /**
     * 密码，永远都是 ****，与长度无关
     */
    PASSWORD,

    /**
     * 电子邮件，9****@qq.com
     */
    EMAIL,

    /**
     * 密钥，永远是*****，与长度无关
     */
    KEY

}
