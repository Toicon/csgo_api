package com.csgo.domain.plus.envelop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@TableName(value = "red_envelop_record")
public class RedEnvelopRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("envelop_item_id")
    private Integer envelopItemId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("user_id")
    private Integer userId;

    @TableField("create_date")
    private Date createDate;

}