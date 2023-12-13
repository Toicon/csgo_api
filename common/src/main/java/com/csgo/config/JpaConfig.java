package com.csgo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author admin
 */
@Configuration
@EnableJpaRepositories("com.csgo.repository")
public class JpaConfig {
}
