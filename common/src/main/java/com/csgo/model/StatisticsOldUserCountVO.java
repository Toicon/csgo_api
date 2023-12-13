package com.csgo.model;

import lombok.Data;
import org.joda.time.DateTime;


/**
 * @author admin
 */
@Data
public class StatisticsOldUserCountVO {

    private DateTime createDateTime;

    private Integer deptId;

    private Integer userCount;

}
