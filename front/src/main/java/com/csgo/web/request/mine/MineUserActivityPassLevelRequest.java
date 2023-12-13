package com.csgo.web.request.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 扫雷-活动闯关
 *
 * @author admin
 */
@Data
public class MineUserActivityPassLevelRequest {

    /**
     * 闯关id
     */
    @ApiModelProperty(notes = "闯关id")
    private Integer id;
    /**
     * 用户选中格子
     */
    @ApiModelProperty(notes = "用户选中格子")
    private Integer selectLattice;
}
