package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

/**
 * 扫雷活动用户闯关信息
 *
 * @author admin
 */
@Data
@TableName("mine_user_activity_pass_level")
public class MineUserActivityPassLevel extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //活动id
    @TableField(value = "activity_id")
    private Integer activityId;
    //第几关
    @TableField(value = "level")
    private Integer level;
    //闯关状态(0:闯关中，1:通过，2:不通过)
    @TableField(value = "pass_state")
    private Integer passState;
    //地雷格子集合（逗号分隔）
    @TableField(value = "mine_lattice")
    private String mineLattice;
    //非地雷格子集合（逗号分隔）
    @TableField(value = "non_mine_lattice")
    private String nonMineLattice;
    //用户选中格子(没选择默认空)
    @TableField(value = "select_lattice")
    private Integer selectLattice;

}
