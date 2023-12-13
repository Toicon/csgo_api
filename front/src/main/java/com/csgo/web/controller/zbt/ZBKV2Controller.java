package com.csgo.web.controller.zbt;

import com.csgo.config.properties.ZBTProperties;
import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizClientException;
import com.csgo.util.HttpUtil2;
import com.csgo.web.request.zbk.FindZBKRequest;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api
@RequestMapping("/zbk")
@Slf4j
public class ZBKV2Controller {
    @Autowired
    private ZBTProperties zbtProperties;

    @PostMapping
    public BaseResponse<List<Object>> find(@Valid @RequestBody FindZBKRequest request) {
        if (CollectionUtils.isEmpty(request.getProductNames())) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("app-key", zbtProperties.getAppKey());
        map.put("appId", zbtProperties.getAppId());
        Gson gson = new Gson();
        List<Object> responseList = new ArrayList<>();
        for (String productName : request.getProductNames()) {
            map.put("keyword", productName);
            String resultJson = HttpUtil2.doGet(zbtProperties.getSearchUrl(), map);
            log.info("result: {}", resultJson);
            Map<String, Object> result = gson.fromJson(resultJson, Map.class);
            if (result.get("data") == null) {
                log.error(String.valueOf(result.get("errorMsg")));
                continue;
            }
            responseList.add(result.get("data"));
        }
        return BaseResponse.<List<Object>>builder().data(responseList).get();
    }

}
