package com.csgo.service.lottery;

import com.csgo.domain.plus.user.UserPlus;
import com.csgo.service.lottery.support.LuckyGift;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public class LotteryDrawBoxCondition {

    private final LuckyGift box;
    private final UserPlus player;
    private final Map<String, String> configMap;
}
