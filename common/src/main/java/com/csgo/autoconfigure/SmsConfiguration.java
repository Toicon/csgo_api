package com.csgo.autoconfigure;

import com.csgo.autoconfigure.properties.SmsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "sms", value = "sousou")
@EnableConfigurationProperties({SmsProperties.class})
public class SmsConfiguration {

}
