package com.csgo.domain.plus.lucky;

import java.io.Serializable;
import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author admin
 */
public enum LotteryDrawType implements IEnum {

    FORMULA, LUCKY, BLOOD, DOWN, NORMAL;

    @Override
    public Serializable getValue() {
        return this.name();
    }
}
