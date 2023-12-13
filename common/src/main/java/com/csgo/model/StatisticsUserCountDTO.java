package com.csgo.model;

import lombok.Data;

import java.util.Date;


/**
 * @author admin
 */
@Data
public class StatisticsUserCountDTO {

    private Date createDate;

    private Integer deptId;

    private Integer userCount;

}
