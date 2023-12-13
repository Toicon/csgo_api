package com.csgo.web.response.blindbox;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class BlindBoxTypeResponse {

    private Integer id;
    private String name;
    private Integer sortId;
    private Date addTime;
    private Date updateTime;
}
