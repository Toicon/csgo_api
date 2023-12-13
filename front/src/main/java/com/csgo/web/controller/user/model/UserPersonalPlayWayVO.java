package com.csgo.web.controller.user.model;

import com.csgo.modular.product.model.front.ProductSimpleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class UserPersonalPlayWayVO {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer userId;

    @ApiModelProperty(value = "全站")
    private PlayWay allWay = null;

    @ApiModelProperty(value = "开箱")
    private PlayWay boxWay = null;

    @ApiModelProperty(value = "Upgrade")
    private PlayWay luckyBoxWay = null;

    @ApiModelProperty(value = "扫雷")
    private PlayWay mineWay = null;

    @Data
    @NoArgsConstructor
    public static class PlayWay {

        @ApiModelProperty(value = "今日最佳饰品")
        private ProductSimpleVO today = null;

        @ApiModelProperty(value = "本周最佳饰品")
        private ProductSimpleVO week = null;

        @ApiModelProperty(value = "本月最佳饰品")
        private ProductSimpleVO month = null;

    }

}
