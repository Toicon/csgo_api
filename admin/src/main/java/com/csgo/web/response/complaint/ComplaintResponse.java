package com.csgo.web.response.complaint;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2021/6/17
 */
@Setter
@Getter
public class ComplaintResponse {

    private String type;

    private String description;

    private List<String> imgs;

    private String telephone;

    private String email;

    private String cb;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
