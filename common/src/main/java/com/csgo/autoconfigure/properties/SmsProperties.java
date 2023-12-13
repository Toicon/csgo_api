package com.csgo.autoconfigure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author admin
 */
@Component
@ConfigurationProperties(prefix = "sms.sousou")
@Getter
@Setter
public class SmsProperties {

    private String appId;
    private String desKey;
    private String md5Key;
    private String tempCode;

}
