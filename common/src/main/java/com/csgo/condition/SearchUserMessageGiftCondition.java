package com.csgo.condition;

import com.csgo.domain.plus.user.UserMessageGiftPlus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserMessageGiftCondition extends Condition<UserMessageGiftPlus> {

    private Date startTime;
    private Date endTime;
    private Integer userId;
}
