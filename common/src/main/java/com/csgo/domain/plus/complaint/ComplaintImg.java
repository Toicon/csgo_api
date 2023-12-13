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
@TableName("complaint_img")
@Setter
@Getter
public class ComplaintImg {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("complaint_id")
    private Integer complaintId;
    @TableField("img")
    private String img;
    @TableField("ct")
    private Date ct;
}
