package com.csgo.domain.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


/**
 * Created by Admin on 2021/5/15
 */
@Setter
@Getter
public class StatisticsDTO {
    //金额
    private BigDecimal amount;

    //数量
    private int count;

    //日期
    private String date;


    //部门id(主播账号归属后台部门)

    private Integer deptId;

    //部门名称(主播账号归属后台部门)
    private String deptName;
}
