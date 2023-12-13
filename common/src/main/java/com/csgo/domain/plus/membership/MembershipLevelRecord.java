package com.csgo.domain.plus.membership;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "membership_level_record")
public class MembershipLevelRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 任务ID
     */
    @TableField("task_id")
    private Integer taskId;
    /**
     * 成长值
     */
    @TableField("growth")
    private BigDecimal growth;
    /**
     * 当前成长值
     */
    @TableField("current_growth")
    private BigDecimal currentGrowth;
    /**
     * 当前等级
     */
    @TableField("current_grade")
    private Integer currentGrade;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    @TableField("ct")
    private Date ct;
}
