package com.csgo.web.request.mine;

import com.echo.framework.platform.web.request.PageRequest;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷活动--用户奖励查询
 *
 * @author admin
 */
@Data
public class MinePrizeQueryRequest extends PageRequest {
    /**
     * 账号
     */
    private String userName;

    private BigDecimal prizePriceMin;

    private BigDecimal prizePriceMax;

}
