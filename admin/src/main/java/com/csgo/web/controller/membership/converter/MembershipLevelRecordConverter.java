package com.csgo.web.controller.membership.converter;

import com.csgo.domain.plus.membership.MembershipLevelRecordDTO;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.response.membership.MembershipLevelRecordResponse;
import org.springframework.stereotype.Service;

@Service
public class MembershipLevelRecordConverter {

    public MembershipLevelRecordResponse to(MembershipLevelRecordDTO entity) {
        MembershipLevelRecordResponse response = new MembershipLevelRecordResponse();
        BeanUtilsEx.copyProperties(entity, response);
        return response;
    }
}
