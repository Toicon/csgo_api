package com.csgo.web.request.envelop;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/4/30
 */
@Setter
@Getter
public class ReceiveEnvelopRequest {

    private Integer redEnvelopId;

    private String token;
}
