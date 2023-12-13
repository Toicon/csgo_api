package com.csgo.sms;

public enum Scenes {
    YZM("SMS_217500100","验证码短信");

    private String key,value;

    Scenes(String key,String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
