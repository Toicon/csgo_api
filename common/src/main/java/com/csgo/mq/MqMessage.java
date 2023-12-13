package com.csgo.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MqMessage {

    private Category category;
    private Type type;
    private String body;

    public enum Category {
        LOTTERY, LUCKY_PRODUCT, MINE, SYSTEM, FISH
    }

    public enum Type {
        LOG, JACKPOT, BOX_JACKPOT, BATTLE_JACKPOT, UPGRADE_JACKPOT, MINE_JACKPOT, BOX_ANCHOR_JACKPOT,
        BOX_JACKPOT_KEY, BOX_ANCHOR_JACKPOT_KEY,
        TEN_DRAW_JACKPOT,
        NOV_BOMB_JACKPOT,
        FISH_USER_JACKPOT,
        FISH_ANCHOR_JACKPOT
    }
}
