package com.csgo.web.response.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户登录日志查询响应
 *
 * @author admin
 */
@Getter
@Setter
public class UserLoginRecordResponse {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
