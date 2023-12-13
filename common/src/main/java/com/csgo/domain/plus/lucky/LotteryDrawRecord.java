package com.csgo.domain.plus.lucky;

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
@Getter
@Setter
@TableName("lottery_draw_record")
public class LotteryDrawRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("user_id")
    private int userId;
    private BigDecimal lucky;
    private LotteryDrawType type;
    @TableField("hit_gift_product_id")
    private int hitGiftProductId;
    private String message;
    private BigDecimal profit;
    private Date ct;
}
