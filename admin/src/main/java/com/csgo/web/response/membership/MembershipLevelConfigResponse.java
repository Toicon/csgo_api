package com.csgo.web.response.membership;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class MembershipLevelConfigResponse {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 等级上限
     */
    private BigDecimal levelLimit;
    /**
     * 等级上限
     */
    private BigDecimal giftRate;
    /**
     * 头像图标
     */
    private String img;
    /**
     * roll房
     */
    private String rollName;
    /**
     * 红包金额
     */
    private BigDecimal redAmount;

    private String cb;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ut;

}
