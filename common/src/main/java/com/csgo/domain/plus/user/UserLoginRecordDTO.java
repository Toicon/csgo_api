package com.csgo.domain.plus.user;

import lombok.Data;

import java.util.Date;

@Data
public class UserLoginRecordDTO {
    
    private Integer id;
    /**
     * 账号
     */
    private String userName;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 最近登录时间
     */
    private Date ct;
}