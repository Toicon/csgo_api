package com.csgo.service.smm.template;


import com.csgo.service.smm.SmsParam;


public class MsgCodeTemplate extends ParameterizedSmsTemplate {

    public MsgCodeTemplate(SmsParam smsParam) {
        super(smsParam);
    }

    @Override
    public String code() {
        return "SMS_460745353";
    }

}
