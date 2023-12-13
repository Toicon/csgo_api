package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 用户注册IP记录
 *
 * @author admin
 */
@Data
@TableName("user_register_ip")
public class UserRegisterIp extends BaseEntity {
    //IP地址
    @TableField(value = "ip")
    private String ip;
    //注册发送短信次数
    @TableField(value = "cnt")
    private Integer cnt;
    //注册日期
    @TableField("reg_date")
    private Date regDate;
}
