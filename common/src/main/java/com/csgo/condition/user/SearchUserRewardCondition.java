package com.csgo.condition.user;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.UserRewardDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserRewardCondition extends Condition<UserRewardDTO> {
    private Integer rewardType;
    private Integer userId;
}
