package com.csgo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author admin
 */
@Data
@ConfigurationProperties(prefix = "tencent")
public class TxProperties {

    private String secretId;

    private String secretKey;

}
