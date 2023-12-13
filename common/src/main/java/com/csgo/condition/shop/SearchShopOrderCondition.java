package com.csgo.condition.shop;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.shop.ShopOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchShopOrderCondition extends Condition<ShopOrder> {

    private Integer giftProductId;
    private Date startDate;
    private Date endDate;
}
