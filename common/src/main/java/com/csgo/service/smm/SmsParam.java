package com.csgo.service.smm;

import java.util.HashMap;
import java.util.Map;

public class SmsParam {

    private final String phone;
    private final Map<String, Object> params = new HashMap<>();

    public SmsParam(String phone) {
        this.phone = phone;
    }

    public static SmsParam of(String phone) {
        return new SmsParam(phone);
    }

    public SmsParam putParam(String key, String value) {
        this.params.putIfAbsent(key, value);
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}


