package com.csgo.modular.tendraw.service.system;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.framework.exception.BizServerException;
import com.csgo.mapper.plus.config.SystemConfigMapper;
import com.csgo.modular.tendraw.model.TenDrawSettingVO;
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
public class TenDrawSystemConfigService {

    public static final String PREFIX = "SYSTEM:TEN_DRAW:";

    public static final String BALL_MAX_SIZE = "SYSTEM:TEN_DRAW:BALL_MAX_SIZE";
    public static final String COLOR_INDEX_GOLD = "SYSTEM:TEN_DRAW:COLOR_INDEX_GOLD";
    public static final String COLOR_INDEX_YELLOW = "SYSTEM:TEN_DRAW:COLOR_INDEX_YELLOW";

    private static final String BASIC_WEIGHT = "SYSTEM:TEN_DRAW:BASIC_WEIGHT";
    private static final String PAY_PRICE_MAX_WEIGHT = "SYSTEM:TEN_DRAW:PAY_PRICE_MAX_WEIGHT";
    private static final String PAY_PRICE_RATE = "SYSTEM:TEN_DRAW:PAY_PRICE_RATE";
    private static final String RETRY_PAY_PRICE_RATE = "SYSTEM:TEN_DRAW:RETRY_PAY_PRICE_RATE";

    private static final String EXP = "SYSTEM:TEN_DRAW:EXP";

    /**
     * 预警值
     */
    public static final String WARN_VALUE = "SYSTEM:TEN_DRAW:WARN_VALUE";

    private final SystemConfigMapper systemConfigMapper;

    public SystemConfig get(String key) {
        return systemConfigMapper.get(key);
    }

    public TenDrawSettingVO getSetting() {
        Map<String, String> configMap = systemConfigMapper.findByPrefix(PREFIX).stream().collect(Collectors.toMap(SystemConfig::getKey, SystemConfig::getValue));
        return toVo(configMap);
    }

    public BigDecimal getWarnValue() {
        SystemConfig systemConfig = get(WARN_VALUE);
        if (systemConfig == null) {
            log.error("[十连配置错误] key:{}", WARN_VALUE);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        BigDecimal value = Convert.toBigDecimal(systemConfig.getValue());
        if (value == null) {
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        return value;
    }

    public Integer getBoxMaxSize() {
        SystemConfig systemConfig = get(BALL_MAX_SIZE);
        if (systemConfig == null) {
            log.error("[十连配置错误] key:{}", BALL_MAX_SIZE);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        return Convert.toInt(systemConfig.getValue());
    }

    private TenDrawSettingVO toVo(Map<String, String> configMap) {
        try {
            Integer ballMaxSize = Convert.toInt(configMap.get(BALL_MAX_SIZE));
            Integer colorIndexGold = Convert.toInt(configMap.get(COLOR_INDEX_GOLD));
            Integer colorIndexYellow = Convert.toInt(configMap.get(COLOR_INDEX_YELLOW));
            BigDecimal basicWeight = Convert.toBigDecimal(configMap.get(BASIC_WEIGHT));
            BigDecimal payPriceMaxWeight = Convert.toBigDecimal(configMap.get(PAY_PRICE_MAX_WEIGHT));
            BigDecimal payPriceRate = Convert.toBigDecimal(configMap.get(PAY_PRICE_RATE));
            BigDecimal retryPayPriceRate = Convert.toBigDecimal(configMap.get(RETRY_PAY_PRICE_RATE));

            Double exp = Convert.toDouble(configMap.get(EXP));
            if (ballMaxSize == null || colorIndexGold == null || colorIndexYellow == null || basicWeight == null ||
                    payPriceMaxWeight == null || payPriceRate == null || retryPayPriceRate == null || exp == null) {
                throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
            }

            TenDrawSettingVO vo = new TenDrawSettingVO();
            vo.setBallMaxSize(ballMaxSize);
            vo.setColorIndexGold(colorIndexGold);
            vo.setColorIndexYellow(colorIndexYellow);
            vo.setBasicWeight(basicWeight);
            vo.setPayPriceMaxWeight(payPriceMaxWeight);
            vo.setPayPriceRate(payPriceRate);
            vo.setRetryPayPriceRate(retryPayPriceRate);
            vo.setExp(exp);
            return vo;
        } catch (Exception e) {
            log.error("[十连配置错误]", e);
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
    }

}
