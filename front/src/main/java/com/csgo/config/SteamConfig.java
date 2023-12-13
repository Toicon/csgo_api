package com.csgo.config;

import org.openid4java.consumer.ConsumerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author admin
 */
@Configuration
public class SteamConfig {

    @Bean
    public ConsumerManager consumerManager() {
        ConsumerManager manager = new ConsumerManager();
        manager.setConnectTimeout(30000);
        manager.setMaxAssocAttempts(0);
        manager.setSocketTimeout(30000);
        return manager;
    }
}
