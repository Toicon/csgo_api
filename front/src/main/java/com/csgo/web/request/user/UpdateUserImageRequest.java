package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class UpdateUserImageRequest {

    private int flag;
    private String img;
}
