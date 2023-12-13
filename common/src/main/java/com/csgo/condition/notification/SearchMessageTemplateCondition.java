package com.csgo.condition.notification;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.message.MessageTemplate;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchMessageTemplateCondition extends Condition<MessageTemplate> {

    private String type;
}
