package com.csgo.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "green.ali")
@Data
public class AliGreenProperty {
    private String accessKey;
    private String secretKey;
}
