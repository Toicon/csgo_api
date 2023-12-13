package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class NovBombHomeVO {

    @ApiModelProperty(value = "最大数量")
    private Integer maxSize;

    @ApiModelProperty(value = "场次列表")
    private List<NovBombConfigVO> list;

}
