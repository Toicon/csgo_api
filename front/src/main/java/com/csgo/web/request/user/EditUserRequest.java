package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/5/1
 */
@Setter
@Getter
public class EditUserRequest {
    private String name;
    private String invitedExtensionCode;
    private Integer extraNum;
    private String steam;
    private String smsCode;
}
