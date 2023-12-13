package com.csgo.condition;

import com.csgo.domain.plus.message.UserMessageRecord;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserMessageRecordCondition extends Condition<UserMessageRecord> {

    private int userId;
}
