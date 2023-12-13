package com.csgo.domain.plus.membership;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>VIP成长值记录表</p>
 * Created by abel_huang on 2021/12/12
 */
@Setter
@Getter
public class MembershipLevelRecordDTO {

    private String userName;

    private String name;
    /**
     * 成长值
     */
    private BigDecimal growth;
    /**
     * 当前成长值
     */
    private BigDecimal currentGrowth;
    /**
     * 当前等级
     */
    private Integer currentGrade;
    /**
     * 备注
     */
    private String remark;

    private Date ct;
}
