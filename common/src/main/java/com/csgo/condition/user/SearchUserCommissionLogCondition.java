package com.csgo.condition.user;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.UserCommissionLogDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserCommissionLogCondition extends Condition<UserCommissionLogDTO> {

    private int userId;
}
