package com.csgo.web.controller.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.config.SearchSystemConfigCondition;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.service.config.SystemConfigService;
import com.csgo.support.DataConverter;
import com.csgo.web.request.config.SearchSystemConfigRequest;
import com.csgo.web.request.config.UpdateSystemConfigRequest;
import com.csgo.web.response.config.SystemConfigResponse;
import com.csgo.web.support.Log;
import com.csgo.web.support.LoginRequired;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author admin
 */
@Api
@LoginRequired
@RequireSession
@RequestMapping("/system/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SiteContext siteContext;

    @PostMapping
    @Log(desc = "查询配置列表")
    public PageResponse<SystemConfigResponse> get(@Valid @RequestBody SearchSystemConfigRequest request) {
        Page<SystemConfig> page = systemConfigService.pagination(DataConverter.to(SearchSystemConfigCondition.class, request));
        return DataConverter.to(config -> {
            SystemConfigResponse response = new SystemConfigResponse();
            BeanUtils.copyProperties(config, response);
            return response;
        }, page);
    }

    @GetMapping
    public BaseResponse<SystemConfigResponse> get(@RequestParam("key") String key) {
        SystemConfig systemConfig = systemConfigService.get(key);
        SystemConfigResponse response = new SystemConfigResponse();
        BeanUtils.copyProperties(systemConfig, response);
        return BaseResponse.<SystemConfigResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新配置")
    public BaseResponse<Void> update(@Valid @RequestBody UpdateSystemConfigRequest request) {
        SystemConfig systemConfig = systemConfigService.get(request.getKey());
        systemConfig.setValue(request.getValue());
        systemConfigService.update(systemConfig, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/lucky-product/level")
    @Log(desc = "配置幸运饰品等级")
    public BaseResponse<Void> updateLuckyProductLevel(@Valid @RequestBody Map<String, String> request) {
        for (Map.Entry<String, String> entry : request.entrySet()) {
            SystemConfig systemConfig = systemConfigService.get("LUCKY_PRODUCT:" + entry.getKey());
            systemConfig.setValue(entry.getValue());
            systemConfigService.update(systemConfig, siteContext.getCurrentUser().getName());
        }
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/{id}")
    @Log(desc = "删除配置")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        systemConfigService.delete(id);
        return BaseResponse.<Void>builder().get();
    }
}
