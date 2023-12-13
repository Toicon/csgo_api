package com.csgo.condition.user;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.UserLoginRecordDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserLoginRecordCondition extends Condition<UserLoginRecordDTO> {
    /**
     * 账号
     */
    private String userName;
    /**
     * IP地址
     */
    private String ip;
}
