package com.csgo.condition.user;

import java.util.Date;
import com.csgo.condition.Condition;
import com.csgo.domain.plus.user.Tag;
import com.csgo.domain.plus.user.UserLuckyRecordDTO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchUserLuckyRecordCondition extends Condition<UserLuckyRecordDTO> {

    private Date startTime;
    private Date endTime;
    private String userName;
    private Integer flag;
    private Tag tag;
}
