package com.csgo.web.response.report;

import com.csgo.config.ColumnValue;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户储值收支汇总
 *
 * @author admin
 */
@Data
public class UserStoredSummaryResponse {
    //日期
    @ColumnValue("日期")
    private String date;
    //上期V币余额
    @ColumnValue("上期V币余额")
    private BigDecimal periodBalance;
    //上期银币余额
    @ColumnValue("上期银币余额")
    private BigDecimal periodDiamondBalance;
    //储值实付
    @ColumnValue("储值实付")
    private BigDecimal paidAmount;
    //赠送卡值
    @ColumnValue("赠送卡值")
    private BigDecimal extraAmount;
    //道具出售
    @ColumnValue("道具出售")
    private BigDecimal propSaleAmount;
    //红包收入
    @ColumnValue("红包收入")
    private BigDecimal redAmount;
    //CDK收入
    @ColumnValue("CDK收入")
    private BigDecimal cdkAmount;
    //商城兑换
    @ColumnValue("商城兑换")
    private BigDecimal mallExchangeAmount;
    //幸运宝箱
    @ColumnValue("幸运宝箱")
    private BigDecimal luckyBoxAmount;
    //本期V币余额
    @ColumnValue("本期V币余额")
    private BigDecimal currentBalance;
    //本期银币余额
    @ColumnValue("本期银币余额")
    private BigDecimal currentDiamondBalance;
}
