package com.csgo.domain.plus.user;

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
public class UserLuckyRecordDTO {

    @TableField("user_name")
    private String userName;
    private Integer flag;
    private Tag tag;
    private LuckyRecordType type;
    @TableField("old_lucky")
    private BigDecimal oldLucky;
    @TableField("new_lucky")
    private BigDecimal newLucky;
    private String cb;
    private Date ct;
}
