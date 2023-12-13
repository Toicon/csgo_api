package com.csgo.condition.complaint;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.complaint.Complaint;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchComplaintCondition extends Condition<Complaint> {
    private String type;

    private String telephone;
}
