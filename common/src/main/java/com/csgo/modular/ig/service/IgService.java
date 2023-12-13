package com.csgo.modular.ig.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.modular.ig.config.IgProperties;
import com.csgo.util.HttpsUtil2;
import com.echo.framework.support.jackson.json.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IgService {

    private final IgProperties igProperties;

    public void checkSteamTradeLinks(String tradeLinks) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("partner_key", igProperties.getPartnerKey());
            params.put("public_key", igProperties.getPublicKey());
            String signParams = igProperties.getPartnerKey() + igProperties.getPublicKey() + igProperties.getSecretKey();
            params.put("sign", DigestUtils.md5Hex(signParams.getBytes(UTF_8)));
            params.put("app_id", igProperties.getAppId());

            params.put("track_link", URLEncoder.encode(tradeLinks, "UTF-8"));

            String igSteamResult = HttpsUtil2.sendFormPost(igProperties.getSteamIdUrl(), params);
            JSONObject steamObj = new JSONObject(igSteamResult);
            int steamCode = steamObj.getInt("code");
            log.info("[Steam链接校验] url:{} data:{}", tradeLinks, steamObj);
            if (steamCode != 1) {
                String msg = steamObj.getString("message");
                log.error("[Steam链接校验 不合法] url:{} data:{} msg:{}", tradeLinks, steamObj, msg);
                throw BizClientException.of(CommonBizCode.STEAM_URL_ILLEGAL);
            }
        } catch (Exception e) {
            throw BizClientException.of(CommonBizCode.STEAM_URL_ILLEGAL);
        }
    }

    public BigDecimal getIgProduct(GiftProductPlus giftProductPlus) {
        Map<String, String> params = new HashMap<>();
        params.put("partner_key", igProperties.getPartnerKey());
        params.put("public_key", igProperties.getPublicKey());
        String signParams = igProperties.getPartnerKey() + igProperties.getPublicKey() + igProperties.getSecretKey();
        params.put("sign", DigestUtils.md5Hex(signParams.getBytes(UTF_8)));
        params.put("app_id", igProperties.getAppId());
        params.put("market_hash_name", giftProductPlus.getEnglishName());

        log.info("IG拉取价格请求参数{}", JSON.toJSON(params));
        String igQueryResult = HttpsUtil2.sendFormPost(igProperties.getQueryPriceUrl(), params);
        log.info("IG拉取价格响应结果：{}", igQueryResult);
        JSONObject jsonObject = new JSONObject(igQueryResult);
        int code = jsonObject.getInt("code");
        if (code != 1) {
            log.warn("IG拉取价格响应 hash:{} code：{}不正确", giftProductPlus.getEnglishName(), code);
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray products = data.getJSONArray("products");
        if (null == products || products.length() == 0) {
            log.warn("IG拉取价格响应 hash:{} 道具不存在", giftProductPlus.getEnglishName());
            return null;
        }
        JSONObject product = products.getJSONObject(0);
        BigDecimal minPrice = product.getBigDecimal("min_price");
        if (minPrice.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("IG拉取价格响应 hash:{} minPrice:{} 不正确", giftProductPlus.getEnglishName(), minPrice);
            return null;
        }
        return minPrice;
    }

}
