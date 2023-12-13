package com.csgo.condition.user;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.RechargeRecordDTO;
import com.csgo.domain.plus.user.Tag;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserRechargeRecordCondition extends Condition<RechargeRecordDTO> {

    private String userName;
    private Integer flag;
    private Tag tag;
}
