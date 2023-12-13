package com.csgo.modular.verify.service;

import com.csgo.modular.verify.config.GeetestConfig;
import com.csgo.modular.verify.sdk.utils.HttpClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeetestApiService {

    private final GeetestConfig geetestConfig;

    private static final String JSON_FORMAT = "1";
    private static final String API_URL = "http://api.geetest.com";
    private static final String REGISTER_URL = API_URL + "/register.php";
    private static final String VALIDATE_URL = API_URL + "/validate.php";

    public String requestRegister(Map<String, String> paramMap) {
        paramMap.put("gt", geetestConfig.getGeetestId());
        paramMap.put("json_format", JSON_FORMAT);
        paramMap.put("sdk", geetestConfig.getVersion());
        String origin_challenge = null;
        try {
            String resBody = HttpClientUtils.doGet(REGISTER_URL, paramMap);
            log.debug("[行为验证][初始化] data:{}", resBody);
            JSONObject jsonObject = new JSONObject(resBody);
            origin_challenge = jsonObject.getString("challenge");
        } catch (Exception e) {
            log.error("[行为验证][初始化] 请求异常，后续流程走宕机模式", e);
            origin_challenge = "";
        }
        return origin_challenge;
    }

    /**
     * 向极验发送二次验证的请求，POST方式
     */
    public String requestValidate(String challenge, String validate, String seccode, Map<String, String> paramMap) {
        paramMap.put("seccode", seccode);
        paramMap.put("json_format", JSON_FORMAT);
        paramMap.put("challenge", challenge);
        paramMap.put("sdk", geetestConfig.getVersion());
        paramMap.put("captchaid", geetestConfig.getGeetestId());

        String response_seccode = null;
        try {
            String resBody = HttpClientUtils.doPost(VALIDATE_URL, paramMap);
            log.debug("[行为验证][二次验证] data:{}", resBody);
            JSONObject jsonObject = new JSONObject(resBody);
            response_seccode = jsonObject.getString("seccode");
        } catch (Exception e) {
            log.error("[行为验证][二次验证] 请求异常，后续流程走宕机模式", e);
            response_seccode = "";
        }
        return response_seccode;
    }

}
