package com.csgo.web.controller.membership;

import com.csgo.condition.membership.SearchMembershipLevelRecordCondition;
import com.csgo.service.membership.MembershipLevelRecordService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.controller.membership.converter.MembershipLevelRecordConverter;
import com.csgo.web.request.membership.SearchMembershipLevelRecordRequest;
import com.csgo.web.response.membership.MembershipLevelRecordResponse;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api
@Slf4j
@RequestMapping("/api/membership/level/record")
public class MembershipLevelRecordController extends BackOfficeController {

    @Autowired
    private MembershipLevelRecordService service;

    @Autowired
    private MembershipLevelRecordConverter converter;

    /**
     * 数据查询
     *
     * @param request
     * @return
     */
    @PostMapping("/pagination")
    public PageResponse<MembershipLevelRecordResponse> pagination(@Valid @RequestBody SearchMembershipLevelRecordRequest request) {
        return DataConverter.to(converter::to, service.pagination(DataConverter.to(SearchMembershipLevelRecordCondition.class, request)));
    }
}
