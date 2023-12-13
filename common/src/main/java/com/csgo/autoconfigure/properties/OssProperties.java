package com.csgo.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author admin
 */
@ConfigurationProperties(prefix = "oss")
@Data
public class OssProperties {

    private OssService service;

    @Data
    public static class OssService {
        private String endpoint;
        private String publicEndpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucket;
        private String dir;
    }
}
