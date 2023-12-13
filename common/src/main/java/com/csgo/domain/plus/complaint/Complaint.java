package com.csgo.domain.plus.complaint;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by Admin on 2021/6/17
 */
@TableName("complaint")
@Setter
@Getter
public class Complaint {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("type")
    private String type;
    @TableField("status")
    private ComplaintStatus status;
    @TableField("description")
    private String description;
    @TableField("email")
    private String email;
    @TableField("telephone")
    private String telephone;
    @TableField("ct")
    private Date ct;

}
