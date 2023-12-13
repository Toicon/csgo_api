package com.csgo.web.response.statistical;

import com.csgo.config.ColumnValue;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author huanghunbao
 */
@Getter
@Setter
public class StatisticalResponse {
    //部门id(主播账号归属后台部门)
    private Integer deptId;
    //父部门ID
    private Integer parentId;
    @ColumnValue("部门名称")
    //部门名称(主播账号归属后台部门)
    private String deptName;
    //日期
    @ColumnValue("日期")
    private String date;
    //开箱次数
    @ColumnValue("开箱次数")
    private int openCount;
    //活跃用户数
    @ColumnValue("活跃用户")
    private int activeCount;
    //每日新增用户数
    @ColumnValue("新增用户")
    private int addCount;
    //老用户数
    @ColumnValue("老用户")
    private int oldCount;
    //每日活跃用户数（每日有充值的人）
    @ColumnValue("活跃用户")
    private int activeNowCount;
    //用户V币余额
    @ColumnValue("用户V币余额")
    private BigDecimal balance;
    //用户银币余额
    @ColumnValue("用户银币余额")
    private BigDecimal diamondBalance;
    //apru 当天用户冲金额除以总充值人数
    @ColumnValue("APRU")
    private BigDecimal APRU;
    //每日充值金额
    @ColumnValue("单日充值金额")
    private BigDecimal addBalance;

}
