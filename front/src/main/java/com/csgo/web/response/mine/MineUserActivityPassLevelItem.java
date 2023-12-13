package com.csgo.web.response.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 扫雷-活动管理
 *
 * @author admin
 */
@Data
public class MineUserActivityPassLevelItem {

    /**
     * 闯关id
     */
    @ApiModelProperty(notes = "闯关id")
    private Integer id;
    /**
     * 第几关
     */
    @ApiModelProperty(notes = "第几关")
    private Integer level;
    /**
     * 闯关状态(0:闯关中，1:通过，2:不通过)
     */
    @ApiModelProperty(notes = "闯关状态(0:闯关中，1:通过，2:不通过)")
    private Integer passState;
    /**
     * 地雷格子集合
     */
    @ApiModelProperty(notes = "地雷格子集合")
    private List<Integer> mineLattice;
    /**
     * 非地雷格子集合
     */
    @ApiModelProperty(notes = "非地雷格子集合")
    private List<Integer> nonMineLattice;
    /**
     * 用户选中格子(没选择默认空)
     */
    @ApiModelProperty(notes = "用户选中格子(没选择默认空)")
    private Integer selectLattice;
}
