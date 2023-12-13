package com.csgo.web.response.code;

import java.math.BigDecimal;
import com.csgo.config.ColumnValue;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class ActivationDownloadView {
    @ColumnValue("物品名称")
    private String productName;
    @ColumnValue("CDK")
    private String cdKey;
    @ColumnValue("价值")
    private BigDecimal price;
}
