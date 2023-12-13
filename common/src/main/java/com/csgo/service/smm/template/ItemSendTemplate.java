package com.csgo.service.smm.template;


import com.csgo.service.smm.SmsParam;


public class ItemSendTemplate extends ParameterizedSmsTemplate {

    public ItemSendTemplate(SmsParam smsParam) {
        super(smsParam);
    }

    @Override
    public String code() {
        return "SMS_460810322";
    }

}
