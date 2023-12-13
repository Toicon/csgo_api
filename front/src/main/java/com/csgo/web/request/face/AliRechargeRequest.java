package com.csgo.web.request.face;

import lombok.Data;

import java.io.Serializable;

/**
 * 腾讯人脸识别支付
 */
@Data
public class AliRechargeRequest implements Serializable {

    private int channelId;
    private int priceItemId;
    private String token;
    private Boolean isApp;
    private int userId;
    /**
     * 认证单据号，人脸核身结果查询
     */
    private String certifyId;
}
