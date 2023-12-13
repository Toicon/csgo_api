package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/5/1
 */
@Setter
@Getter
public class EditUserPasswordRequest {
    private String password;
    private String oldPassword;
}
