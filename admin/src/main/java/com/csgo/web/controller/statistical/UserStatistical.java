package com.csgo.web.controller.statistical;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class UserStatistical {
    //开箱次数
    private int openCount;
    //活跃用户数
    private int activeCount;
    //每日新增用户数
    private int addCount;
    //老用户数
    private int oldCount;
    //每日活跃用户数（每日有充值的人）
    private int activeNowCount;
    //用户V币余额
    private BigDecimal balance = BigDecimal.ZERO;
    //用户钻石余额
    private BigDecimal diamondBalance = BigDecimal.ZERO;
    //背包余额
    private BigDecimal backpackBalance = BigDecimal.ZERO;
    //每日充值金额
    private BigDecimal addBalance = BigDecimal.ZERO;
    //apru 当天用户冲金额除以总充值人数
    private BigDecimal APRU = BigDecimal.ZERO;
    //日期
    private String date;
    // 每个人的当前余额
    private BigDecimal currentAmount = BigDecimal.ZERO;
    //父部门ID
    private Integer parentId;
    //是否叶子节点
    private boolean hasChild;
    //部门id(主播账号归属后台部门)
    private Integer deptId;
    //部门名称(主播账号归属后台部门)
    private String deptName;
}
