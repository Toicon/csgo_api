package com.csgo.condition.shop;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.UserCurrencyExchange;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class SearchUserCurrencyExchangeCondition extends Condition<UserCurrencyExchange> {
    private Integer userId;
}
