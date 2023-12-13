package com.csgo.web.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.SearchUserMessageCondition;
import com.csgo.domain.plus.user.UserMessageDTO;
import com.csgo.framework.util.RUtil;
import com.csgo.modular.product.enums.ProductKindEnums;
import com.csgo.modular.product.model.dto.UserMessageKeyStatisticsDTO;
import com.csgo.service.UserMessageService;
import com.csgo.support.DataConverter;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.user.SearchUserMessageRequest;
import com.csgo.web.response.user.UserMessageResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Api
@LoginRequired
@RequireSession
@RequestMapping("/user/message")
public class UserMessageController {

    @Autowired
    private UserMessageService service;
    @Autowired
    private SiteContext siteContext;

    /**
     * 我的背包
     */
    @PostMapping("/pagination")
    public PageResponse<UserMessageResponse> pagination(@Valid @RequestBody SearchUserMessageRequest request) {
        Integer productKind = request.getProductKind();
        if (productKind == null) {
            request.setProductKind(ProductKindEnums.NORMAL.getCode());
        }

        SearchUserMessageCondition condition = DataConverter.to(SearchUserMessageCondition.class, request);
        condition.setUserId(siteContext.getCurrentUser().getId());
        Page<UserMessageDTO> page = service.pagination(condition);
        return DataConverter.to(userMessageDTO -> {
            UserMessageResponse response = new UserMessageResponse();
            BeanUtils.copyProperties(userMessageDTO, response);
            return response;
        }, page);
    }

    /**
     * 钥匙统计
     */
    @GetMapping("/key-statistics")
    public BaseResponse<List<UserMessageKeyStatisticsDTO>> getKeyStatistics() {
        Integer userId = siteContext.getCurrentUser().getId();
        List<UserMessageKeyStatisticsDTO> list = service.getKeyStatistics(userId);

        return RUtil.ok(list);
    }

}
