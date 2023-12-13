package com.csgo.web.response.code;

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
public class ActivationCodeResponse {
    private int id;
    private String userName;
    private String productName;
    private BigDecimal price;
    private String cdKey;
    /**
     * 标识符,1-内部用户,0-散户
     */
    private Integer flag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveDate;
    /**
     * 目标账号
     */
    private String targetUserName;
}
