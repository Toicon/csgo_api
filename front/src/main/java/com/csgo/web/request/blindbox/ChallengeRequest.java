package com.csgo.web.request.blindbox;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class ChallengeRequest {

    private String blindBoxRoomNum;
    private BigDecimal price;
    private boolean wait;
    private String password;
}
