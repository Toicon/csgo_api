package com.csgo.web.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class UserMessageRecordResponse {
    private Integer id;
    private Integer userId;
    private String num;
    private String source;
    private String operation;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    private List<UserMessageProductRecordResponse> productList;
}
