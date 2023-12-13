package com.csgo.web.response.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class InvitedUserResponse {

    private String img;
    private String name;
    //上级邀请码
    private String extensionCode;
}
