package com.csgo.service.lottery.support;

import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public class LotteryDrawResult {

    private final LuckyGiftProduct hitProduct;
    private final LotteryDrawRecord record;
}
