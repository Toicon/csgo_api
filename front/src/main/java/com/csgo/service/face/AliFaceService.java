package com.csgo.service.face;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayUserCertifyOpenCertifyRequest;
import com.alipay.api.request.AlipayUserCertifyOpenInitializeRequest;
import com.alipay.api.request.AlipayUserCertifyOpenQueryRequest;
import com.alipay.api.response.AlipayUserCertifyOpenCertifyResponse;
import com.alipay.api.response.AlipayUserCertifyOpenInitializeResponse;
import com.alipay.api.response.AlipayUserCertifyOpenQueryResponse;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.csgo.config.properties.AliProperties;
import com.csgo.exception.ServiceErrorException;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.web.request.face.AliFaceRequest;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 阿里人脸识别
 */
@Slf4j
@Service
public class AliFaceService {
    @Autowired
    private AliProperties aliProperties;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    //支付宝人脸识别网关地址
    private final static String ALI_GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    //人脸识别三次过期1天
    private static final Long ERROR_FACE_EXPIRE_IN_SECOND = 60 * 60 * 24L;

    /**
     * 人脸认证初始化
     *
     * @param aliFaceRequest
     * @return
     */
    private String init(AliFaceRequest aliFaceRequest, String callUrl) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(ALI_GATEWAY_URL, aliProperties.getAppId(),
                    aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");
            AlipayUserCertifyOpenInitializeRequest request = new AlipayUserCertifyOpenInitializeRequest();
            JSONObject bizContent = new JSONObject();
            JSONObject identityParam = new JSONObject();
            identityParam.put("identity_type", "CERT_INFO");
            identityParam.put("cert_type", "IDENTITY_CARD");
            identityParam.put("cert_name", aliFaceRequest.getName());
            identityParam.put("cert_no", aliFaceRequest.getIdNo());
            bizContent.put("identity_param", identityParam);
            JSONObject merchantConfigParam = new JSONObject();
            merchantConfigParam.put("return_url", callUrl);
            bizContent.put("merchant_config", merchantConfigParam);
            bizContent.put("outer_order_no", aliFaceRequest.getOrderNo());
            bizContent.put("biz_code", "FACE");
            request.setBizContent(bizContent.toString());
            AlipayUserCertifyOpenInitializeResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response.getCertifyId();
            }
            throw new ServiceErrorException("初始化人脸识别失败，请联系管理员");
        } catch (Exception e) {
            log.error("初始化人脸识别失败:{}", e);
            throw new ServiceErrorException("初始化人脸识别失败，请联系管理员");
        }
    }

    /**
     * 获取人脸核身地址
     *
     * @param aliFaceRequest
     * @return
     */
    public String getH5FaceUrl(AliFaceRequest aliFaceRequest, String callUrl) {
        //人脸核验初始化信息
        String certifyId = init(aliFaceRequest, callUrl);
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(ALI_GATEWAY_URL, aliProperties.getAppId(),
                    aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");
            AlipayUserCertifyOpenCertifyRequest request = new AlipayUserCertifyOpenCertifyRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("certify_id", certifyId);
            request.setBizContent(bizContent.toString());
            AlipayUserCertifyOpenCertifyResponse response = alipayClient.pageExecute(request, "GET");
            if (response.isSuccess()) {
                aliFaceRequest.setCertifyId(certifyId);
                return response.getBody();
            }
            throw new ServiceErrorException("获取人脸认证失败，请联系管理员");
        } catch (Exception e) {
            log.error("获取人脸认证失败:{}", e);
            throw new ServiceErrorException("获取人脸认证失败，请联系管理员");
        }
    }

    /**
     * 校验身份证信息
     *
     * @param aliFaceRequest
     */
    public void checkUserIdentityInfo(AliFaceRequest aliFaceRequest) {
        /*String orderNo = UUID.randomUUID().toString().replace("-", "");
        String nonce = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> params = new HashMap<>();
        params.put("webankAppId", tencentProperties.getAppId());
        params.put("orderNo", orderNo);
        params.put("name", tencentFaceRequest.getName());
        params.put("idNo", tencentFaceRequest.getIdNo());
        params.put("userId", tencentFaceRequest.getUserId());
        params.put("version", tencentProperties.getVersion());
        params.put("nonce", nonce);
        String accessToken = this.getAccessToken();
        String faceTicket = this.getFaceTicket(accessToken);
        List<String> faceSignList = new ArrayList<>();
        faceSignList.add(tencentProperties.getAppId());
        faceSignList.add(tencentFaceRequest.getUserId());
        faceSignList.add(tencentProperties.getVersion());
        faceSignList.add(nonce);
        String faceSign = this.sign(faceSignList, faceTicket);
        params.put("sign", faceSign);
        String url = tencentProperties.getServiceFaceUrl() + tencentProperties.getH5FaceIdApi() + "?orderNo=" + orderNo;
        String result = HttpUtil2.doPostJSON(url, JSON.toJSON(params));
        log.info("返回人脸核身信息>>{}", result);
        Map resultMap = JSON.fromJSON(result, Map.class);
        if (resultMap == null) {
            throw new ServiceErrorException("获取人脸核身地址失败");
        }
        String code = !resultMap.containsKey("code") ? "" : String.valueOf(resultMap.get("code"));
        String msg = !resultMap.containsKey("msg") ? "" : String.valueOf(resultMap.get("msg"));
        if (StringUtils.isEmpty(code) || !TencentFaceCodeEnum.YES.getCode().equals(code)) {
            if (TencentFaceCodeEnum.IDENTITY_ERROR.getCode().equals(code)) {
                throw new ServiceErrorException(msg);
            }
        }*/
    }

    /**
     * 检查人脸是否通过
     *
     * @param certifyId
     * @param userId
     * @return
     */
    public boolean checkFace(String certifyId, Integer userId) {
        boolean isPass;
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(ALI_GATEWAY_URL, aliProperties.getAppId(),
                    aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");
            AlipayUserCertifyOpenQueryRequest request = new AlipayUserCertifyOpenQueryRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("certify_id", certifyId);
            request.setBizContent(bizContent.toString());
            AlipayUserCertifyOpenQueryResponse response = alipayClient.execute(request);
            Map resultMap = null;
            String passed = "";
            String result = response.getBody();
            if (!StringUtils.isEmpty(result)) {
                JSONObject jsonObject = JSON.fromJSON(result, JSONObject.class);
                resultMap = (Map) jsonObject.get("alipay_user_certify_open_query_response");
                passed = resultMap.get("passed") == null ? "" : resultMap.get("passed").toString();
            }
            if (response.isSuccess() && resultMap != null && "T".equals(passed)) {
                isPass = true;
            } else {
                log.error("人脸核身核验失败:{}", response.getFailReason());
                //记录核身核验失败记录
                String errorCount = redisTemplateFacde.get(getErrorFaceUserKey(String.valueOf(userId)));
                if (!StringUtils.isEmpty(errorCount)) {
                    int cnt = Integer.valueOf(errorCount) + 1;
                    redisTemplateFacde.set(getErrorFaceUserKey(String.valueOf(userId)), String.valueOf(cnt), ERROR_FACE_EXPIRE_IN_SECOND);
                } else {
                    redisTemplateFacde.set(getErrorFaceUserKey(String.valueOf(userId)), "1", ERROR_FACE_EXPIRE_IN_SECOND);
                }
                isPass = false;
            }
        } catch (Exception e) {
            log.error("人脸认证查询失败:{}", e);
            throw new ServiceErrorException("人脸认证查询失败，请联系管理员");
        }
        return isPass;
    }

    /**
     * 人脸失败用户的redis key
     *
     * @return
     */
    private String getErrorFaceUserKey(String userId) {
        return Messages.format("user:errorFaceUser:{}", userId);
    }

}
