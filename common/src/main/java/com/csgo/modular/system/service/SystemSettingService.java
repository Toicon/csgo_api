package com.csgo.modular.system.service;

import cn.hutool.core.util.StrUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.SystemSettingConstant;
import com.csgo.framework.exception.BizServerException;
import com.csgo.modular.system.domain.SystemSettingDO;
import com.csgo.modular.system.mapper.SystemSettingMapper;
import com.csgo.modular.system.model.FrontSystemSettingVO;
import com.csgo.modular.system.model.admin.SystemSettingUpdateByKeyVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private static final String KEY_PREFIX = "system:config:";

    private final SystemSettingMapper systemSettingMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public FrontSystemSettingVO getConfig() {
        String verifyVersion = this.getCacheOrDefault(SystemSettingConstant.SETTING_VERIFY_VERSION, "0");

        FrontSystemSettingVO vo = new FrontSystemSettingVO();
        vo.setVerifyVersion(verifyVersion);
        return vo;
    }

    public SystemSettingDO getSettingByKey(String configKey) {
        SystemSettingDO entity = systemSettingMapper.getByKey(configKey);
        if (entity == null) {
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        return entity;
    }

    public SystemSettingDO updateSettingByKey(SystemSettingUpdateByKeyVM vm) {
        SystemSettingDO entity = systemSettingMapper.getByKey(vm.getConfigKey());
        if (entity == null) {
            throw BizServerException.of(CommonBizCode.COMMON_DATA_NOT_FOUND);
        }
        entity.setConfigValue(vm.getConfigValue());
        systemSettingMapper.updateById(entity);

        setCache(vm.getConfigKey(), vm.getConfigValue());
        return entity;
    }

    public String getSettingByKey(String key, String defaultVal) {
        SystemSettingDO entity = systemSettingMapper.getByKey(key);
        if (entity == null) {
            return defaultVal;
        }
        return entity.getConfigValue();
    }

    public String getCacheOrDefault(String key, String defaultVal) {
        String cacheKey = KEY_PREFIX + key;
        String val = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isBlank(val)) {
            String dbValue = getSettingByKey(key, defaultVal);
            setCache(cacheKey, dbValue);
            return defaultVal;
        }
        return val;
    }

    public void setCache(String key, String value) {
        String cacheKey = KEY_PREFIX + key;
        stringRedisTemplate.opsForValue().set(cacheKey, value);
    }

}
