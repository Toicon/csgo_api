package com.csgo.web.response.order;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/5/4
 */
@Setter
@Getter
@ColumnWidth(value = 20)
public class OrderRecordExcel {

    @ExcelProperty("用户名")
    private String userPhone;

    @ExcelProperty("订单号")
    private String orderNum;

    @ExcelProperty("支付金额")
    private BigDecimal paidAmount;

    @ExcelProperty("实付订单金额")
    private BigDecimal realPaidAmount;

    @ExcelProperty("订单金额")
    private BigDecimal orderAmount;

    @ExcelProperty("支付时间")
    private String paidTime;

    @ExcelProperty("创建时间")
    private String createTime;

}
