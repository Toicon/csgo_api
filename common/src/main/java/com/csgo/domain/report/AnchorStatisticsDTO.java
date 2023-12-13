package com.csgo.domain.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/5/15
 */
@Setter
@Getter
public class AnchorStatisticsDTO {
    //主播名
    private String name;

    //主播ID
    private Integer userId;

    //主播账号
    private String userName;

    //活跃用户
    private int activeUser;

    //新增用户
    private int newUser;

    //有效用户
    private int validUser;

    //老用户
    private int oldUser;

    //活跃付费用户
    private int chargeCount;

    //推广用户充值
    private BigDecimal extensionCharge;

    //主播充值
    private BigDecimal anchorCharge;

    //部门id(主播账号归属后台部门)

    private Integer deptId;

    //部门名称(主播账号归属后台部门)
    private String deptName;
}
