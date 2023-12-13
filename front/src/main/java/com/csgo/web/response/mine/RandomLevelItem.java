package com.csgo.web.response.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 扫雷-随机地雷
 *
 * @author admin
 */
@Data
public class RandomLevelItem {

    /**
     * 地雷格子集合
     */
    @ApiModelProperty(notes = "地雷格子集合")
    private String mineLattice;
    /**
     * 非地雷格子集合
     */
    @ApiModelProperty(notes = "非地雷格子集合")
    private String nonMineLattice;

}
