package com.csgo.web.request.blindbox;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
public class BlindBoxRequest {

    @NotBlank(message = "盲盒名称不能为空")
    private String name;

    @NotNull(message = "盲盒类型不能为空")
    private Integer typeId;

    private Integer type;

    private Integer sortId;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private Integer grade;

    @NotBlank(message = "盲盒展示图不能为空")
    private String img;

    @NotBlank(message = "盲盒宝箱图不能为空")
    private String boxImg;
}