package com.csgo.web.controller.user;


import com.csgo.domain.plus.anchor.UserAnchorWhite;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.service.UserAnchorWhiteService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.user.UserAnchorWhiteResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 测试开箱白名单
 */
@Api
@RequestMapping("/user/anchor/white")
@Slf4j
public class AdminUserAnchorWhiteController extends BackOfficeController {

    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserAnchorWhiteService userAnchorWhiteService;


    @PutMapping("/change/{userId}")
    @Log(desc = "修改测试开箱白名单状态")
    public BaseResponse<Void> innerRechargeWhite(@PathVariable("userId") Integer userId, @RequestParam("innerRecharge") boolean innerRecharge) {
        if (innerRecharge) {
            userAnchorWhiteService.addWhite(userId, siteContext.getCurrentUser().getName());
        } else {
            userAnchorWhiteService.delWhite(userId, siteContext.getCurrentUser().getName());
        }
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 获取所有测试开箱白名单列表
     *
     * @return
     */
    @GetMapping("/findAllList")
    public BaseResponse<List<UserAnchorWhiteResponse>> findAllList() {
        List<UserAnchorWhite> limits = userAnchorWhiteService.findAllList();
        if (CollectionUtils.isEmpty(limits)) {
            return BaseResponse.<List<UserAnchorWhiteResponse>>builder().get();
        }
        List<Integer> ids = limits.stream().map(UserAnchorWhite::getUserId).collect(Collectors.toList());
        List<UserPlus> userPluses = userPlusMapper.findByIds(ids);
        return BaseResponse.<List<UserAnchorWhiteResponse>>builder().data(toWhiteListResponseList(limits, userPluses)).get();
    }


    private List<UserAnchorWhiteResponse> toWhiteListResponseList(List<UserAnchorWhite> userAnchorWhites, List<UserPlus> userPluses) {
        Map<Integer, UserPlus> userPlusMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(userPluses)) {
            userPlusMap = userPluses.stream().collect(Collectors.toMap(UserPlus::getId, Function.identity()));
        }
        List<UserAnchorWhiteResponse> responses = new ArrayList<>();
        for (UserAnchorWhite userAnchorWhite : userAnchorWhites) {
            UserAnchorWhiteResponse response = new UserAnchorWhiteResponse();
            response.setCreateBy(userAnchorWhite.getCreateBy());
            response.setCreateDate(userAnchorWhite.getCreateDate());
            response.setUserId(userAnchorWhite.getUserId());
            UserPlus user = userPlusMap.get(userAnchorWhite.getUserId());
            if (null != user) {
                response.setName(user.getName());
                response.setPhone(user.getUserName());
            }
            responses.add(response);
        }
        return responses;
    }


}
