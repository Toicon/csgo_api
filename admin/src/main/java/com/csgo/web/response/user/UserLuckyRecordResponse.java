package com.csgo.web.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class UserLuckyRecordResponse {

    private String userName;
    private BigDecimal oldLucky;
    private BigDecimal newLucky;
    private String type;
    private Integer flag;
    private String cb;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
