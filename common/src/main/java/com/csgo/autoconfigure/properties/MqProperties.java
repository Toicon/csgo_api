package com.csgo.autoconfigure.properties;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * @author admin
 */
@ConfigurationProperties(prefix = "mq")
@Data
public class MqProperties {

    private String accessKey;
    private String secretKey;
    private String nameSrvAddr;
    private MqService consumer;
    private MqService producer;
    private String signName;

    @Data
    public static class MqService {
        private boolean enable;
    }

    public Properties createProperties() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, nameSrvAddr);
        return properties;
    }
}
