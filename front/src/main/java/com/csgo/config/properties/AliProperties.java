package com.csgo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
@ConfigurationProperties(prefix = "ali.pay")
@Data
public class AliProperties {
    private String appId;
    private String selfAppId;
    private String privateKey;
    private String selfPrivateKey;
    private String aliPayPublicKey;
    private String selfAliPayPublicKey;
    private String notifyUrl;
    private String selfNotifyUrl;
    private String returnUrl;
    private String selfReturnUrl;
    /**
     * 人脸核身结果返回及跳转
     */
    private String faceCallUrl;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 实名认证回调前端地址
     */
    private String realNameCallUrl;
}
