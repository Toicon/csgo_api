package com.csgo.web.controller.envelop.converter;

import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.domain.enums.RedEnvelopStatus;
import com.csgo.service.envelop.RedEnvelopService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.request.envelop.EditRedEnvelopRequest;
import com.csgo.web.response.envelop.RedEnvelopResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by abel_huang
 */
@Service
public class RedEnvelopConverter {
    @Autowired
    private RedEnvelopService redEnvelopService;

    public RedEnvelop to(EditRedEnvelopRequest request, String createBy) {
        RedEnvelop redEnvelop = new RedEnvelop();
        BeanUtilsEx.copyProperties(request, redEnvelop);
        redEnvelop.setStatus(RedEnvelopStatus.valueOf(request.getStatus()));
        redEnvelop.setCreateBy(createBy);
        return redEnvelop;
    }

    public RedEnvelop to(int id, EditRedEnvelopRequest request, String updateBy) {
        RedEnvelop redEnvelop = redEnvelopService.get(id);
        BeanUtilsEx.copyProperties(request, redEnvelop);
        redEnvelop.setStatus(RedEnvelopStatus.valueOf(request.getStatus()));
        redEnvelop.setUpdateBy(updateBy);
        return redEnvelop;
    }

    public RedEnvelopResponse to(RedEnvelop redEnvelop) {
        RedEnvelopResponse response = new RedEnvelopResponse();
        BeanUtilsEx.copyProperties(redEnvelop, response);
        response.setStatus(redEnvelop.getStatus().name());
        return response;
    }

}
