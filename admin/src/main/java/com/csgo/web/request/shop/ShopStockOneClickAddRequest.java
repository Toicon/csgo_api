package com.csgo.web.request.shop;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author admin
 */
@Data
public class ShopStockOneClickAddRequest {

    @Size(min = 1, message = "请至少勾选一项")
    private List<Long> shopIds = Lists.newArrayList();

    @NotNull(message = "库存数不允许为空")
    @Min(value = 1, message = "库存需要大于0")
    private Integer stock;

}
