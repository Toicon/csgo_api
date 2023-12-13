package com.csgo.web.controller.user;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.web.response.gift.UserExtractMessageGiftResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.csgo.condition.SearchUserMessageGiftCondition;
import com.csgo.domain.Scale;
import com.csgo.mapper.ScaleMapper;
import com.csgo.service.gift.UserMessageGiftService;
import com.csgo.service.user.BalanceSupportService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.gift.SearchUserMessageGiftRequest;
import com.csgo.web.request.gift.SellProductRequest;
import com.csgo.web.response.gift.UserMessageGiftResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;


@Api
@LoginRequired
@RequireSession
@RequestMapping("/user/message/gift")
@Slf4j
public class UserMessageGiftController {

    @Autowired
    private UserMessageGiftService userMessageGiftService;
    @Autowired
    private ScaleMapper scaleMapper;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private BalanceSupportService balanceSupportService;

    @PostMapping("/pagination")
    public PageResponse<UserMessageGiftResponse> pagination(
            @RequestParam(required = false, name = "dateRangeType") Integer dateRangeType,
            @Valid @RequestBody SearchUserMessageGiftRequest request) {
        Integer userId = siteContext.getCurrentUser().getId();

        Page<UserMessageGiftPlus> pagination = userMessageGiftService.getFrontPage(dateRangeType, userId, request);

        return DataConverter.to(userMessageGiftPlus -> {
            UserMessageGiftResponse response = new UserMessageGiftResponse();
            BeanUtils.copyProperties(userMessageGiftPlus, response);
            return response;
        }, pagination);
    }

    /**
     * 出售个人商品的信息
     *
     * @return
     */
    @ApiOperation("出售个人商品的信息（前台）")
    @PutMapping(value = "sell")
    public BaseResponse<Void> sell(@Valid @RequestBody SellProductRequest request) {
        String userMessageIds = request.getUserMessageId();
        if (userMessageIds == null || userMessageIds.equals("")) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        List<Scale> scales = scaleMapper.selectList(new Scale());
        Scale se;
        if (CollectionUtils.isEmpty(scales)) {
            log.error("商品出售失败,未设置出售折算率");
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        se = scales.get(0);
        BigDecimal b = se.getScale();
        String[] userMessageId = userMessageIds.split(",");
        balanceSupportService.sell(siteContext.getCurrentUser().getId(), request, userMessageId, b);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 用户饰品提取查询
     *
     * @param request
     * @return
     */
    @PostMapping("/extractPage")
    public PageResponse<UserExtractMessageGiftResponse> extractPage(@Valid @RequestBody SearchUserMessageGiftRequest request) {
        SearchUserMessageGiftCondition condition = DataConverter.to(SearchUserMessageGiftCondition.class, request);
        condition.setUserId(siteContext.getCurrentUser().getId());
        return DataConverter.to(userMessageGiftPlus -> {
            UserExtractMessageGiftResponse response = new UserExtractMessageGiftResponse();
            BeanUtils.copyProperties(userMessageGiftPlus, response);
            return response;
        }, userMessageGiftService.extractPage(condition));
    }
}
