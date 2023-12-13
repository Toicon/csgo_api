package com.csgo.condition;

import com.csgo.domain.plus.user.UserMessageDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserMessageCondition extends Condition<UserMessageDTO> {

    private Integer productKind;

    private int userId;
    private int state;
}
