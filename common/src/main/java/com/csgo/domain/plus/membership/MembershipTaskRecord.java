package com.csgo.domain.plus.membership;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.JdbcType;

/**
 * @author admin
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@TableName(value = "membership_task_record", autoResultMap = true)
public class MembershipTaskRecord extends BaseEntity {
    @TableField("user_id")
    private int userId;

    @TableField("rule_count")
    private int ruleCount;

    @TableField(value = "record_status", typeHandler = MembershipTaskStatus.TypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private MembershipTaskStatus recordStatus;

    @TableField(value = "rule_type", typeHandler = MembershipTaskRule.TypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private MembershipTaskRule ruleType;

    @TableField("invite_user_id")
    private Integer inviteUserId;

}
