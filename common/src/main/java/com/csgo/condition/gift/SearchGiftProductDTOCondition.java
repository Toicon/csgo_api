package com.csgo.condition.gift;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.gift.GiftProductDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchGiftProductDTOCondition extends Condition<GiftProductDTO> {

    private String giftProductName;

    private String type;

    private String orderStock;
}
