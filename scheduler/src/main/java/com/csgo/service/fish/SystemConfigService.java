package com.csgo.service.fish;

import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class SystemConfigService {
    private static final String BOX_WARN_VALUE = "LUCKY_WARN:BOX_WARN_VALUE";
    private static final String BATTLE_WARN_VALUE = "LUCKY_WARN:BATTLE_WARN_VALUE";
    private static final String BOX_ANCHOR_WARN_VALUE = "LUCKY_WARN:BOX_ANCHOR_WARN_VALUE";
    private static final String FISH_WARN_VALUE = "LUCKY_WARN:FISH_WARN_VALUE";
    @Autowired
    private SystemConfigMapper systemConfigMapper;

    public SystemConfig get(String key) {
        return systemConfigMapper.get(key);
    }

    // @Cacheable(value = "PREFIX_CONFIG_MAP", key = "#prefix")
    public SystemConfigFacade findByPrefix(String prefix) {
        SystemConfigFacade systemConfigFacade = new SystemConfigFacade(systemConfigMapper.findByPrefix(prefix).stream().collect(Collectors.toMap(SystemConfig::getKey, SystemConfig::getValue)));
        SystemConfig config = get(BOX_WARN_VALUE);
        if (null != config) {
            systemConfigFacade.setWarnValue(new BigDecimal(config.getValue()));
        }
        SystemConfig anchorConfig = get(BOX_ANCHOR_WARN_VALUE);
        if (null != anchorConfig) {
            systemConfigFacade.setWarnAnchorValue(new BigDecimal(anchorConfig.getValue()));
        }
        SystemConfig battleConfig = get(BATTLE_WARN_VALUE);
        if (null != battleConfig) {
            systemConfigFacade.setBattleWarnValue(new BigDecimal(battleConfig.getValue()));
        }
        SystemConfig fishConfig = get(FISH_WARN_VALUE);
        if (null != fishConfig) {
            systemConfigFacade.setFishWarnValue(new BigDecimal(fishConfig.getValue()));
        }
        return systemConfigFacade;
    }
}
