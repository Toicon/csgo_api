package com.csgo.condition.config;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.config.SystemConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchSystemConfigCondition extends Condition<SystemConfig> {
    private String prefix;
}
