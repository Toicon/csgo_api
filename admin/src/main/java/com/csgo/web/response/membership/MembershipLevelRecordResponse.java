package com.csgo.web.response.membership;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class MembershipLevelRecordResponse {
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 用户昵称
     */
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
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;

}
