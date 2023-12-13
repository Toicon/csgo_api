package com.csgo.service.smm.template;

import com.csgo.service.smm.SmsParam;
import com.csgo.service.smm.Template;
import com.echo.framework.support.jackson.json.JSON;

public abstract class ParameterizedSmsTemplate implements Template {

    private final SmsParam smsParam;

    public ParameterizedSmsTemplate(SmsParam smsParam) {
        this.smsParam = smsParam;
    }

    @Override
    public String getParams() {
        return JSON.toJSON(smsParam.getParams());
    }

    @Override
    public String getPhone() {
        return smsParam.getPhone();
    }
}
