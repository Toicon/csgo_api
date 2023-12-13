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
 * <p>用户VIP表</p>
 * Created by abel_huang on 2021/12/12
 */
@Setter
@Getter
@TableName(value = "membership")
public class Membership {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Integer userId;
    /**
     * 等级
     */
    @TableField("grade")
    private Integer grade;
    /**
     * 成长值
     */
    @TableField("growth")
    private BigDecimal growth;


    @TableField("real_name")
    private String realName;


    @TableField("id_card")
    private String idCard;


    @TableField("img")
    private String img;


    @TableField("birthday")
    private Date birthday;

    @TableField("last_grade_date")
    private Date lastGradeDate;

    @TableField("ct")
    private Date ct;

    @TableField("ut")
    private Date ut;
}
