package com.csgo.modular.backpack.model.admin;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
public class AdminGiftKeyUserMessageVM {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotNull(message = "饰品ID不能为空")
    private Integer productId;

    @NotNull(message = "饰品数量不能为空")
    @Min(value = 1, message = "饰品数量最小值为 1")
    @Max(value = 10, message = "饰品数量最大值为 10")
    private Integer productNum;

}
