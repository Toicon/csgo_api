package com.csgo.web.request.membership;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class EditMembershipLevelConfigRequest {
    /**
     * 等级
     */
    private Integer level;
    /**
     * 等级上限
     */
    private BigDecimal levelLimit;
    /**
     * 加送奖励
     */
    private BigDecimal giftRate;
    /**
     * 头像图标
     */
    private String img;
    /**
     * 等级图标
     */
    private String rollName;
    /**
     * 红包金额
     */
    private BigDecimal redAmount;

}
