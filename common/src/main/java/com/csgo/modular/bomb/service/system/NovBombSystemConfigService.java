package com.csgo.modular.bomb.service.system;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.framework.exception.BizServerException;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import com.csgo.modular.bomb.model.NovBombSettingVO;
import com.csgo.util.Convert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombSystemConfigService {

    public static final String PREFIX = "SYSTEM:NOV_BOMB:";

    private static final String EXP = "SYSTEM:NOV_BOMB:EXP";

    /**
     * 预警值
     */
    public static final String WARN_VALUE = "SYSTEM:NOV_BOMB:WARN_VALUE";

    private final SystemConfigMapper systemConfigMapper;

    public SystemConfig get(String key) {
        return systemConfigMapper.get(key);
    }

    public NovBombSettingVO getSetting() {
        Map<String, String> configMap = systemConfigMapper.findByPrefix(PREFIX).stream().collect(Collectors.toMap(SystemConfig::getKey, SystemConfig::getValue));
        return toVo(configMap);
    }

    public BigDecimal getWarnValue() {
        SystemConfig systemConfig = get(WARN_VALUE);
        if (systemConfig == null) {
            log.error("[模拟拆弹配置错误] key:{}", WARN_VALUE);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        BigDecimal value = Convert.toBigDecimal(systemConfig.getValue());
        if (value == null) {
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        return value;
    }

    private NovBombSettingVO toVo(Map<String, String> configMap) {
        try {
            BigDecimal exp = Convert.toBigDecimal(configMap.get(EXP));
            if (exp == null) {
                throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
            }
            NovBombSettingVO vo = new NovBombSettingVO();
            vo.setExp(exp);
            return vo;
        } catch (Exception e) {
            log.error("[模拟拆弹配置错误]", e);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
    }

}
