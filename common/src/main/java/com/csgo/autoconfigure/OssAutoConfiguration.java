package com.csgo.autoconfigure;

import com.csgo.autoconfigure.properties.OssProperties;
import com.csgo.upload.AliOSSConfig;
import com.csgo.upload.AliOSSService;
import com.csgo.upload.ObjectStorageService;
import com.csgo.upload.StorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author admin
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {
    @Autowired
    private OssProperties properties;

    @Bean
    public StorageConfig config() {
        OssProperties.OssService service = properties.getService();
        return AliOSSConfig.Builder.config()
                .endpoint(service.getEndpoint())
                .accessKeyId(service.getAccessKeyId())
                .accessKeySecret(service.getAccessKeySecret())
                .bucketName(service.getBucket())
                .publicEndpoint(service.getPublicEndpoint())
                .directory(service.getDir())
                .build();
    }

    @Bean(name = "storageService")
    public ObjectStorageService storageService() {
        return new AliOSSService((AliOSSConfig) config());
    }

}
