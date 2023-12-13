package com.csgo.service.smm;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.csgo.autoconfigure.sms.SmsProperty;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.echo.framework.support.jackson.json.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Service
public class AliSmsService {

    private static Logger logger = LoggerFactory.getLogger(AliSmsService.class);

    private static final String RESPONSE_OK_CODE = "OK";

    @Autowired
    private SmsProperty smsProperty;

    public boolean send(Template smsTemplate) {
        if (null == smsTemplate || !StringUtils.hasText(smsTemplate.getPhone())) {
            return false;
        }
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperty.getAccessKey(), smsProperty.getSecretKey());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", smsTemplate.getPhone());
        request.putQueryParameter("SignName", smsProperty.getSignName());
        request.putQueryParameter("TemplateCode", smsTemplate.code());
        request.putQueryParameter("TemplateParam", smsTemplate.getParams());
        try {
            CommonResponse response = client.getCommonResponse(request);
            if (null == response) {
                return false;
            }
            logger.info("send sms response:{}, status:{}.", response.getData(), response.getHttpStatus());
            SmsResult smsResult = JSON.fromJSON(response.getData(), SmsResult.class);
            if (RESPONSE_OK_CODE.equals(smsResult.getCode())) {
                return true;
            } else {
                throw new BusinessException(ExceptionCode.SEND_MSG_ERROR);
            }
        } catch (ClientException e) {
            logger.error(e.getMessage(), e);
            throw new BusinessException(ExceptionCode.SEND_MSG_ERROR);
        }
    }

    @Data
    private static class SmsResult {

        @JsonProperty("Message")
        private String message;
        @JsonProperty("RequestId")
        private String requestId;
        @JsonProperty("Code")
        private String code;
        @JsonProperty("BizId")
        private String bizId;


    }
}
