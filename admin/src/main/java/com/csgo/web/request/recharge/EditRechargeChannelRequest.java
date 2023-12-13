package com.csgo.web.request.recharge;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class EditRechargeChannelRequest {

    private String type;
    private String method;
    private Integer sortId;
    private Boolean hidden;
}
