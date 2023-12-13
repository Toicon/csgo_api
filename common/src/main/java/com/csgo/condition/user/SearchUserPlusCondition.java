package com.csgo.condition.user;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.Tag;
import com.csgo.domain.plus.user.UserPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserPlusCondition extends Condition<UserPlus> {
    private String userName;
    private String inviteUser;
    private Boolean frozen;
    private Integer status;
    private Integer flag;
    private String startTime;
    private String endTime;
    private Tag tag;
    private boolean isInner;
    private Integer parentId;
    private String extensionCode;
}
