package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("user_platform_reward_record")
public class UserPlatformRewardRecordDO {

    public static final Integer TYPE_REAL_NAME_VERIFY = 0;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("type")
    private Integer type;

    @TableField("user_id")
    private Integer userId;

    @TableField("money")
    private BigDecimal money;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_date")
    private Date createDate;

    @TableField("create_by")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_date")
    private Date updateDate;

    @TableField("update_by")
    private String updateBy;

}
