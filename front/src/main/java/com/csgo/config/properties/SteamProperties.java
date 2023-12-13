package com.csgo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
@ConfigurationProperties(prefix = "steam")
@Data
public class SteamProperties {

    private String callback;
    private String openIdUrl;
    private String apiUrl;
    private String key;
    private String frontUrl;
}
