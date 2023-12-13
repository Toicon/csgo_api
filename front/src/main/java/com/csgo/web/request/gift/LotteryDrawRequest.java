package com.csgo.web.request.gift;

import lombok.Data;

/**
 * @author admin
 */
@Data
public class LotteryDrawRequest {

    private int id;
    private int num;
    private String token;
}
