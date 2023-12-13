package com.csgo.framework.sensitive;

/**
 * @author admin
 */
public class SensitiveConditionAlwaysTrue implements SensitiveCondition {

    @Override
    public boolean encrypt() {
        return true;
    }

}
