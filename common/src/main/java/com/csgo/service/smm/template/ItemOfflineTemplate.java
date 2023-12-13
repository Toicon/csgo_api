package com.csgo.service.smm.template;


import com.csgo.service.smm.SmsParam;


public class ItemOfflineTemplate extends ParameterizedSmsTemplate {

    public ItemOfflineTemplate(SmsParam smsParam) {
        super(smsParam);
    }

    @Override
    public String code() {
        return "SMS_460785378";
    }

}
