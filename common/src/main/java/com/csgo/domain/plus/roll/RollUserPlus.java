package com.csgo.domain.plus.roll;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/5/21
 */
@TableName("roll_user")
@Setter
@Getter
public class RollUserPlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("userId")
    private Integer userId;
    @TableField("rollId")
    private Integer rollId;
    @TableField("img")
    private String img;
    @TableField("isappoint")
    private String isappoint;
    @TableField("rollname")
    private String rollname;
    @TableField("username")
    private String username;
    @TableField("flag")
    private String flag;
    @TableField("ut")
    private Date ut;
    @TableField("ct")
    private Date ct;
    @TableField(value = "rollgiftId", updateStrategy = FieldStrategy.IGNORED)
    private Integer rollgiftId;
    @TableField(value = "rollgiftImg", updateStrategy = FieldStrategy.IGNORED)
    private String rollgiftImg;
    @TableField(value = "rollgiftName", updateStrategy = FieldStrategy.IGNORED)
    private String rollgiftName;
    @TableField(value = "rollgiftPrice", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal rollgiftPrice;
    @TableField(value = "rollgiftGrade", updateStrategy = FieldStrategy.IGNORED)
    private String rollgiftGrade;
}
