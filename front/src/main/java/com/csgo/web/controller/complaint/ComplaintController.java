package com.csgo.web.controller.complaint;

import com.csgo.domain.plus.complaint.Complaint;
import com.csgo.service.complaint.ComplaintService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.request.complaint.ComplaintRequest;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * Created by Admin on 2021/5/22
 */
@Api
@RequireSession
@RequestMapping("/complaint")
@Slf4j
public class ComplaintController {
    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public BaseResponse<Void> complaint(@Valid @RequestBody ComplaintRequest request) {
        Complaint complaint = new Complaint();
        BeanUtilsEx.copyProperties(request, complaint);
        complaint.setType(request.getType());
        complaintService.insert(complaint, request.getImgs());
        return BaseResponse.<Void>builder().get();
    }
}
