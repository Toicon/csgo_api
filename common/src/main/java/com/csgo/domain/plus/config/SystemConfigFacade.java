package com.csgo.domain.plus.config;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @author admin
 */
@Slf4j
public class SystemConfigFacade {

    public SystemConfigFacade(Map<String, String> map) {
        this.map = map;
    }

    private BigDecimal warnValue;

    private BigDecimal warnAnchorValue;

    private BigDecimal battleWarnValue;

    private BigDecimal fishWarnValue;

    private final Map<String, String> map;

    public BigDecimal rate(String key) {
        return decimal(key).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal decimal(String key) {
        if (!map.containsKey(key)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(map.get(key));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getWarnValue() {
        return warnValue;
    }

    public void setWarnValue(BigDecimal warnValue) {
        this.warnValue = warnValue;
    }

    public BigDecimal getBattleWarnValue() {
        return battleWarnValue;
    }

    public void setBattleWarnValue(BigDecimal battleWarnValue) {
        this.battleWarnValue = battleWarnValue;
    }

    public BigDecimal getWarnAnchorValue() {
        return warnAnchorValue;
    }

    public void setWarnAnchorValue(BigDecimal warnAnchorValue) {
        this.warnAnchorValue = warnAnchorValue;
    }

    public BigDecimal getFishWarnValue() {
        return fishWarnValue;
    }

    public void setFishWarnValue(BigDecimal fishWarnValue) {
        this.fishWarnValue = fishWarnValue;
    }
}
