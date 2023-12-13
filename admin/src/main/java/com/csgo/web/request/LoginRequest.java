package com.csgo.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class LoginRequest {

    private String username;
    private String password;
    private String code;
    private String verificationCode;
}
