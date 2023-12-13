package com.csgo.web.controller.roll;

import cn.hutool.core.util.StrUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.RollInfoView;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.response.RollGiftResponse;
import com.csgo.framework.exception.BizClientException;
import com.csgo.service.roll.RollHelp;
import com.csgo.service.roll.RollService;
import com.csgo.service.roll.RollUserService;
import com.csgo.service.user.UserService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.roll.FindRollRequest;
import com.csgo.web.response.roll.RollDetailResponse;
import com.csgo.web.response.roll.RollInviteGiftResponse;
import com.csgo.web.response.roll.RollInviteResponse;
import com.csgo.web.response.roll.RollResponse;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Api
@RequireSession
@RequestMapping("/roll")
@io.swagger.annotations.Api(tags = "ROLL房相关接口")
@Slf4j
public class RollController {

    @Autowired
    private RollService rollService;
    @Autowired
    private UserService userService;
    @Autowired
    private RollUserService rollUserService;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private RollHelp rollHelp;

    @ApiOperation(value = "获取roll房邀请链接接口", notes = "获取roll房邀请链接接口")
    @GetMapping("/invite/{id}")
    public BaseResponse<RollInviteResponse> getInviteInfo(@PathVariable("id") Integer id) {
        RollPlus roll = rollService.get(id);
        if (null == roll.getAnchorUserId()) {
            return BaseResponse.<RollInviteResponse>builder().get();
        }
        UserPlus user = userService.get(roll.getAnchorUserId());
        if (null == user) {
            return BaseResponse.<RollInviteResponse>builder().get();
        }
        RollInviteResponse response = new RollInviteResponse();
        response.setInviteCode(user.getExtensionCode());
        response.setNickname(user.getName());
        RollInfoView view = rollHelp.getRollInfo(roll.getId());
        for (RollGiftResponse giftResponse : view.getGiftResponses()) {
            RollInviteGiftResponse inviteGiftResponse = new RollInviteGiftResponse();
            inviteGiftResponse.setGiftImg(giftResponse.getImg());
            inviteGiftResponse.setGiftName(giftResponse.getProductname());
            inviteGiftResponse.setGiftPrice(giftResponse.getPrice());
            response.getGiftResponses().add(inviteGiftResponse);
        }
        return BaseResponse.<RollInviteResponse>builder().data(response).get();
    }

    @GetMapping("/{id}")
    public BaseResponse<RollResponse> get(@PathVariable("id") Integer id) {
        RollPlus roll = rollService.get(id);

        RollResponse response = new RollResponse();
        BeanUtilsEx.copyProperties(roll, response);
        response.setRollType(roll.getRollType().name());
        response.setNeedPassword(StrUtil.isNotBlank(roll.getPassword()));
        if (null != response.getAnchorUserId()) {
            UserPlus user = userService.get(response.getAnchorUserId());
            if (null != user) {
                response.setImg(user.getImg());
            }
        }
        UserInfo userInfo = siteContext.getCurrentUser();
        if (null != userInfo) {
            List<RollUserPlus> rollUserList = rollUserService.find(userInfo.getId(), id);
            response.setJoined(!CollectionUtils.isEmpty(rollUserList));
        }
        return BaseResponse.<RollResponse>builder().data(response).get();
    }

    @LoginRequired
    @RequireSession
    @GetMapping("/current")
    public BaseResponse<List<RollDetailResponse>> findByUserId() {
        Set<Integer> rollIds = rollUserService.findByUserId(siteContext.getCurrentUser().getId()).stream().map(RollUserPlus::getRollId).collect(Collectors.toSet());
        List<RollDetailResponse> responses = to(rollService.find(rollIds));
        return BaseResponse.<List<RollDetailResponse>>builder().data(responses).get();
    }

    @PostMapping
    public BaseResponse<List<RollDetailResponse>> find(@Valid @RequestBody FindRollRequest request) {
        List<RollPlus> rolls = rollService.find(request.getStatus());
        List<RollDetailResponse> responses = to(rolls);
        return BaseResponse.<List<RollDetailResponse>>builder().data(responses).get();
    }

    @GetMapping("/top3")
    public BaseResponse<List<RollDetailResponse>> findTop3() {
        List<RollPlus> rolls = rollService.findTop3();
        List<RollDetailResponse> responses = to(rolls);
        return BaseResponse.<List<RollDetailResponse>>builder().data(responses).get();
    }

    @GetMapping("/topN")
    public BaseResponse<List<RollDetailResponse>> findTopN(@RequestParam(defaultValue = "2") Integer num) {
        if (num < 1) {
            log.error("num 不能小于1");
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        if (num > 30) {
            log.error("num 不能大于30");
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        List<RollPlus> rolls = rollService.findTopN(num);
        List<RollDetailResponse> responses = to(rolls);
        return BaseResponse.<List<RollDetailResponse>>builder().data(responses).get();
    }


    private List<RollDetailResponse> to(List<RollPlus> rolls) {
        long now = new Date().getTime();
        if (CollectionUtils.isEmpty(rolls)) {
            return Collections.EMPTY_LIST;
        }
        Map<Integer, UserPlus> userMap = userService.findByIds(rolls.stream().map(RollPlus::getAnchorUserId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(UserPlus::getId, user -> user));
        return rolls.stream().map(roll -> this.to(roll, userMap, now)).collect(Collectors.toList());
    }

    private RollDetailResponse to(RollPlus roll,
                                  Map<Integer, UserPlus> userMap,
                                  long now) {
        RollDetailResponse response = new RollDetailResponse();
        BeanUtils.copyProperties(roll, response);
        if (userMap.containsKey(roll.getAnchorUserId())) {
            response.setImg(userMap.get(roll.getAnchorUserId()).getImg());
        }
        RollInfoView view = rollHelp.getRollInfo(roll.getId());
        response.setProducts(view.getGiftResponses());
        response.setTotalPrice(view.getTotalPrice());
        response.setDiamondTotalPrice(view.getDiamondTotalPrice());
        response.setTotalGiftNum(view.getTotalGiftNum());
        //根据rollid查询到当前房间参与的人数
        response.setUsernum(view.getUserNum());
        response.setRemainingTime((roll.getDrawDate().getTime() - now) / 1000);
        return response;
    }
}
