package com.csgo.domain.report;

import com.csgo.config.ColumnValue;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 用户储值收支汇总
 * by admin
 */
@Data
public class GoodDeliverySummaryDto {
    //商品名称
    @ColumnValue("商品名称")
    private String productName;
    //销售数量
    @ColumnValue("销售数量")
    private Long saleCount;
    //出货金额
    @ColumnValue("出货金额")
    private BigDecimal igSaleMoney;
}
