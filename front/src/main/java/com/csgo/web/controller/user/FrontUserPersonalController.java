package com.csgo.web.controller.user;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.util.RUtil;
import com.csgo.service.user.FrontUserStatisticsService;
import com.csgo.web.controller.user.model.*;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author admin
 */

@Slf4j
@Api
@io.swagger.annotations.Api(tags = "用户-个人中心")
@RequestMapping("/user/personal")
@RequiredArgsConstructor
@RequireSession
public class FrontUserPersonalController {

    private final SiteContext siteContext;

    private final FrontUserStatisticsService frontUserStatisticsService;

    private Integer resolveUserId() {
        UserInfo currentUser = siteContext.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw BizClientException.of(CommonBizCode.USER_NOT_FOUND);
        }
        return currentUser.getId();
    }

    @ApiOperation("个人结算-头部")
    @GetMapping("/statistics-user")
    public BaseResponse<UserPersonalVO> getUser(@RequestParam(value = "userId", required = false) Integer userId) {
        if (userId == null) {
            userId = resolveUserId();
        }

        UserPersonalVO vo = frontUserStatisticsService.getUser(userId);
        return RUtil.ok(vo);
    }

    @ApiOperation("个人结算-充值统计")
    @GetMapping("/statistics-recharge")
    public BaseResponse<UserPersonalRechargeVO> getRecharge(@RequestParam(value = "userId", required = false) Integer userId) {
        if (userId == null) {
            userId = resolveUserId();
        }

        UserPersonalRechargeVO vo = frontUserStatisticsService.getRecharge(userId);
        return RUtil.ok(vo);
    }

    @ApiOperation("个人结算-开箱统计")
    @GetMapping("/statistics-open-box")
    public BaseResponse<UserPersonalOpenBoxVO> getOpenBox(@RequestParam(value = "userId", required = false) Integer userId) {
        if (userId == null) {
            userId = resolveUserId();
        }

        UserPersonalOpenBoxVO vo = frontUserStatisticsService.getOpenBox(userId);
        return RUtil.ok(vo);
    }

    @ApiOperation("个人结算-战绩奖励")
    @GetMapping("/statistics-score")
    public BaseResponse<UserPersonalScoreVO> getScore(@RequestParam(value = "userId", required = false) Integer userId) {
        if (userId == null) {
            userId = resolveUserId();
        }

        UserPersonalScoreVO vo = frontUserStatisticsService.getScore(userId);
        return RUtil.ok(vo);
    }

    @ApiOperation("个人结算-战绩表现")
    @GetMapping("/statistics-play-way")
    public BaseResponse<UserPersonalPlayWayVO> getPlayWay(@RequestParam(value = "userId", required = false) Integer userId) {
        if (userId == null) {
            userId = resolveUserId();
        }

        UserPersonalPlayWayVO vo = frontUserStatisticsService.getPlayWay(userId);
        return RUtil.ok(vo);
    }

}
