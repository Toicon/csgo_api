package com.csgo.condition.notification;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.message.MessageNotification;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchMessageNotificationCondition extends Condition<MessageNotification> {

    private Integer userId;
}
