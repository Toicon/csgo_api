package com.csgo.web.request.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class UpdateSystemConfigRequest {

    private String key;
    private String value;
}
