package com.csgo.modular.verify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
@ConfigurationProperties(prefix = "geetest")
@Data
public class GeetestConfig {

    private String geetestId = "d826b9f47f7d06e60f211a5e401d7e21";
    private String geetestKey = "23310aa01588d18e4cf5bb8805417484";

    private String version = "jave-servlet:3.1.1";

}
