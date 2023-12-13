package com.csgo.service.smm.template;


import com.csgo.service.smm.SmsParam;

/**
 * 全局更新短信模板
 */
public class GlobalUpdateTemplate extends ParameterizedSmsTemplate {

    public GlobalUpdateTemplate(SmsParam smsParam) {
        super(smsParam);
    }

    @Override
    public String code() {
        return "SMS_460751150";
    }

}
