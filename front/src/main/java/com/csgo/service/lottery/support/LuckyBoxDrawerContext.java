package com.csgo.service.lottery.support;

import com.csgo.domain.Gift;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class LuckyBoxDrawerContext {

    private final Gift gift;

    public Boolean drawGold;

    /**
     * 次数
     */
    public Integer loop;

    public LuckyBoxDrawerContext(Gift gift) {
        this.gift = gift;
        this.drawGold = false;
    }

    public boolean mustDrawGold() {
        if (drawGold || !gift.getNewPeopleSwitch()) {
            return false;
        }
        return loop == 5;
    }

}
