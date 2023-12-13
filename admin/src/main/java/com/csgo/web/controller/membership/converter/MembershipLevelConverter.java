package com.csgo.web.controller.membership.converter;

import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.service.envelop.RedEnvelopService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.request.membership.EditMembershipLevelConfigRequest;
import com.csgo.web.response.membership.MembershipLevelConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MembershipLevelConverter {
    @Autowired
    private RedEnvelopService redEnvelopService;

    public MembershipLevelConfig to(EditMembershipLevelConfigRequest request, MembershipLevelConfig entity) {
        BeanUtilsEx.copyProperties(request, entity);
        return entity;
    }

    public MembershipLevelConfigResponse to(MembershipLevelConfig entity) {
        MembershipLevelConfigResponse response = new MembershipLevelConfigResponse();
        BeanUtilsEx.copyProperties(entity, response);
        RedEnvelop redEnvelop = redEnvelopService.findByGrade(entity.getLevel());
        response.setRedAmount(redEnvelop == null ? BigDecimal.ZERO : redEnvelop.getMinAmount());
        return response;
    }
}
