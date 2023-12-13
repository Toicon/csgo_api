package com.csgo.web.request.roll;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class InsertRollCoinsRequest {

    @Min(value = 1, message = "数量必须大于1")
    private int num;

    private Integer rollId;

    @NotNull(message = "金币不能为空")
    private BigDecimal amount;

    @NotBlank(message = "金币图片不能为空")
    private String img;
}
