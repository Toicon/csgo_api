package com.csgo.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Created by Admin on 2021/5/25
 */
@Component
@ConfigurationProperties(prefix = "zbt")
@Data
public class ZBTProperties {

    private String appKey;

    private Integer appId;

    private String apiUrl;

    private String quicklyBuy;

    private String searchUrl;

    private String priceUrl;

    private String orderQuery;

    private String productFilter;

    private String steamUrl;

    private String steamCheck;
}
