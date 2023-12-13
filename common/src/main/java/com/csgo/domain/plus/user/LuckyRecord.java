package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@TableName("lucky_record")
@Getter
@Setter
public class LuckyRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("user_id")
    private int userId;
    private LuckyRecordType type;
    @TableField("old_lucky")
    private BigDecimal oldLucky;
    @TableField("new_lucky")
    private BigDecimal newLucky;
    private String cb;
    private Date ct;
}
