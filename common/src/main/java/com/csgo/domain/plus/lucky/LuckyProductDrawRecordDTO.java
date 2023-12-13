package com.csgo.domain.plus.lucky;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyProductDrawRecordDTO {

    @TableField("userName")
    private String userName;
    @TableField("userImg")
    private String userImg;
    @TableField("giftName")
    private String giftName;
    @TableField("giftImg")
    private String giftImg;
    @TableField("lucky")
    private BigDecimal lucky;
    @TableField("price")
    private BigDecimal price;
    @TableField("createTime")
    private Date createTime;
}
