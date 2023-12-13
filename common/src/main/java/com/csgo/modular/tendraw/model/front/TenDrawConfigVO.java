package com.csgo.modular.tendraw.model.front;

import com.csgo.modular.tendraw.support.TenDrawBall;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class TenDrawConfigVO {

    @ApiModelProperty(value = "最大球数")
    private Integer ballMaxSize;

    @ApiModelProperty(value = "一堆球")
    private List<TenDrawBall> ballList;

}
