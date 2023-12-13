package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode
public class TenDrawPreviewVM {

    private List<Ball> ballList;

    @Data
    public static class Ball {

        @ApiModelProperty(value = "魂球ID")
        private Integer id;

        @ApiModelProperty(value = "魂球数量")
        private Integer num;

    }

}
