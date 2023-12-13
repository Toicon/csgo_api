package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

/**
 * 后台管理用户数据过滤
 *
 * @author admin
 */
@Data
@TableName("admin_report_filter")
public class AdminReportFilter extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
}
