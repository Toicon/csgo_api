package com.csgo.web.response.report;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.csgo.config.ColumnValue;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/5/15
 */
@Setter
@Getter
@ColumnWidth(value = 20)
public class AnchorStatisticsResponse {

    @ColumnValue("部门名称")
    @ExcelProperty("部门名称")
    //部门名称(主播账号归属后台部门)
    private String deptName;

    @ExcelProperty("经纪人")
    @ExcelIgnore
    private String adminUserName;

    //主播名
    @ColumnValue("主播名")
    @ExcelProperty("主播名")
    private String name;

    //主播账号
    @ColumnValue("主播账号")
    @ExcelProperty("主播账号")
    private String userName;

    //活跃用户
    @ColumnValue("活跃用户")
    @ExcelProperty("活跃用户")
    @ExcelIgnore
    private int activeUser;

    //新增用户
    @ColumnValue("新增用户")
    @ExcelProperty("新增用户")
    @ExcelIgnore
    private int newUser;

    //有效用户
    @ColumnValue("有效用户")
    @ExcelProperty("有效用户")
    @ExcelIgnore
    private int validUser;

    //老用户
    @ColumnValue("老用户")
    @ExcelProperty("老用户")
    @ExcelIgnore
    private int oldUser;

    //活跃付费用户
    @ColumnValue("活跃付费用户")
    @ExcelProperty("活跃付费用户")
    @ExcelIgnore
    private int chargeCount;

    //ARUP值
    @ColumnValue("ARUP值")
    @ExcelProperty("ARUP值")
    @ExcelIgnore
    private BigDecimal profit;

    //推广用户充值
    @ColumnValue("推广用户充值")
    @ExcelProperty("推广用户充值")
    private BigDecimal extensionCharge;

    //主播充值
    @ColumnValue("主播充值")
    @ExcelProperty("主播充值")
    private BigDecimal anchorCharge;

    //部门id(主播账号归属后台部门)
    @ExcelIgnore
    private Integer deptId;

}
