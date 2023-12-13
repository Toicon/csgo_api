package com.csgo.web.response.operation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by Admin on 2021/5/30
 */
@Setter
@Getter
public class OperationLogResponse {
    private String description;
    private String params;
    private String responses;
    private String uri;
    private Integer userId;
    private String userName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
