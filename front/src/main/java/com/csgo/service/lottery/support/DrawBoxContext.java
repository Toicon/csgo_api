package com.csgo.service.lottery.support;

import com.csgo.domain.Gift;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class DrawBoxContext {

    private final Integer userId;

    private final Gift gift;

    public DrawBoxContext(Integer userId, Gift gift) {
        this.userId = userId;
        this.gift = gift;
    }

    public boolean isKeyMode() {
        return "碎片专区".equals(gift.getType());
    }

}
