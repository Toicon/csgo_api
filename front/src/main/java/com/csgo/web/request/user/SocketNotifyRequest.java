package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class SocketNotifyRequest {

    private List<Integer> userPrizeId;
    private String from;
}
