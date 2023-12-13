package com.csgo.modular.tendraw.model.admin;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class AdminTenDrawBallUpdateVM {

    @NotNull(message = "ID不能为空")
    private Integer id;

    @NotBlank(message = "名称不能为空")
    @Length(max = 32, message = "不能超过32个字符")
    private String name;

    @NotBlank(message = "图片地址不能为空")
    private String img;

    @NotNull(message = "权重不能为空")
    private BigDecimal weight;

}
