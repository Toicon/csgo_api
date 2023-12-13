package com.csgo.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class LoginSmsRequest {

    private String phone;
    private String smsCode;

}
