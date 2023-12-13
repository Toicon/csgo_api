package com.csgo.web.controller.membership;

import com.csgo.condition.membership.SearchMembershipLevelConfigCondition;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.service.membership.MembershipLevelConfigService;
import com.csgo.support.BusinessException;
import com.csgo.support.DataConverter;
import com.csgo.support.ExceptionCode;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.controller.membership.converter.MembershipLevelConverter;
import com.csgo.web.request.membership.EditMembershipLevelConfigRequest;
import com.csgo.web.request.membership.SearchMembershipLevelConfigRequest;
import com.csgo.web.response.membership.MembershipLevelConfigResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Api
@Slf4j
@RequestMapping("/api/membership/level/config")
public class MembershipLevelConfigController extends BackOfficeController {

    @Autowired
    private MembershipLevelConfigService service;

    @Autowired
    private MembershipLevelConverter converter;

    /**
     * 数据查询
     *
     * @param request
     * @return
     */
    @PostMapping("/pagination")
    public PageResponse<MembershipLevelConfigResponse> pagination(@Valid @RequestBody SearchMembershipLevelConfigRequest request) {
        return DataConverter.to(converter::to, service.pagination(DataConverter.to(SearchMembershipLevelConfigCondition.class, request)));
    }

    /**
     * 添加数据
     *
     * @param request
     * @return
     */
    @PostMapping
    @Log(desc = "添加VIP等级配置")
    public BaseResponse<Void> save(@Valid @RequestBody EditMembershipLevelConfigRequest request) {
        service.save(converter.to(request, new MembershipLevelConfig()), request.getRedAmount(), siteContext.getCurrentUser().getUsername());
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 更新数据
     *
     * @param id,request
     */
    @PutMapping("/{id}")
    @Log(desc = "编辑VIP等级配置")
    public BaseResponse<Void> update(@PathVariable(value = "id") Integer id, @Valid @RequestBody EditMembershipLevelConfigRequest request) {
        MembershipLevelConfig entity = service.get(id);
        if (null == entity) {
            log.info("找不到MembershipLevel更新：{}", id);
            throw new BusinessException(ExceptionCode.RESOURCE_NOT_FOUND);
        }
        service.update(converter.to(request, entity), request.getRedAmount(), siteContext.getCurrentUser().getUsername());
        return BaseResponse.<Void>builder().get();
    }
}
