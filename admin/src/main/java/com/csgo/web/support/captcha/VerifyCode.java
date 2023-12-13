package com.csgo.web.support.captcha;

import lombok.Data;

/**
 * @author admin
 */
@Data
public class VerifyCode {
    private String code;

    private byte[] imgBytes;

    private long expireTime;
}
