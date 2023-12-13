package com.csgo.domain.plus.jackpot;

import java.math.BigDecimal;
import org.apache.ibatis.type.JdbcType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("jackpot_operation_record")
public class JackpotOperationRecord extends BaseEntity {

    @TableField("before_amount")
    private BigDecimal beforeAmount;
    @TableField("amount")
    private BigDecimal amount;
    @TableField(value = "jackpot_type", typeHandler = JackpotType.TypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private JackpotType jackpotType;
}
