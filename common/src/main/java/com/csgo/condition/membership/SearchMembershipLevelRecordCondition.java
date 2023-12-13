package com.csgo.condition.membership;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.membership.MembershipLevelRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by abel_huang on 2021/12/12
 */
@Setter
@Getter
public class SearchMembershipLevelRecordCondition extends Condition<MembershipLevelRecord> {
    private String userName;

    private Date startDate;

    private Date endDate;
}
