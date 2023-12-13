package com.csgo.autoconfigure.sms;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(SmsProperty.class)
@Configuration
public class SMSServerConfig {


}
