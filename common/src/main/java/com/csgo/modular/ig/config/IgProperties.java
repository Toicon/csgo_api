package com.csgo.modular.ig.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 2021/5/25
 */
@Component
@ConfigurationProperties(prefix = "ig")
@Data
public class IgProperties {

    private String partnerKey;

    private String publicKey;

    private String secretKey;

    private String apiUrl;

    private String appId;

    //指定品类购买
    public String getBuyUrl() {
        return apiUrl + "/third/api/product_buy";
    }

    //商品品类列表查询
    public String getQueryPriceUrl() {
        return apiUrl + "/third/api/product_list";
    }

    //获取steamid
    public String getSteamIdUrl() {
        return apiUrl + "/third/api/steamid";
    }

    //获取订单信息
    public String getOrderUrl() {
        return apiUrl + "/third/api/get_partner_order";
    }

    //获取报价信息
    public String getTradeOfferUrl() {
        return apiUrl + "/third/api/tradeoffer";
    }
}
