package com.csgo.autoconfigure.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ConfigurationProperties("sms.ali")
public class SmsProperty {

    private String accessKey;

    private String secretKey;

    private String signName;

}
