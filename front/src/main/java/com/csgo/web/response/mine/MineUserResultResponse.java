package com.csgo.web.response.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 扫雷-获取奖励信息
 *
 * @author admin
 */
@Data
public class MineUserResultResponse {
    /**
     * 挑战状态(1:成功，2:失败，3：挑战结束)
     */
    @ApiModelProperty(notes = "挑战状态(1:成功，2:失败，3：挑战结束)")
    private Integer prizeState;
    /**
     * 奖品名称
     */
    @ApiModelProperty(notes = "奖品名称")
    private String giftProductName;
    /**
     * 奖品图片
     */
    @ApiModelProperty(notes = "奖品图片")
    private String giftProductImg;
    /**
     * 奖品商品价格
     */
    @ApiModelProperty(value = "奖品商品价格")
    private BigDecimal giftProductPrice;

    /**
     * 对应崭新出厂这样的标签
     */
    @ApiModelProperty(value = "对应崭新出厂这样的标签")
    private String exteriorName;

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
