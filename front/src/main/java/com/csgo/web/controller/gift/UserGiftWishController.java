package com.csgo.web.controller.gift;

import com.csgo.framework.util.RUtil;
import com.csgo.modular.giftwish.model.front.UserGiftWishRewardVO;
import com.csgo.modular.giftwish.model.front.UserGiftWishVM;
import com.csgo.modular.giftwish.model.front.UserGiftWishVO;
import com.csgo.modular.giftwish.service.UserGiftWishService;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author admin
 */
@Slf4j
@Api
@io.swagger.annotations.Api(tags = "开箱心愿")
@RequestMapping("/gift/wish")
@RequiredArgsConstructor
@LoginRequired
@RequireSession
public class UserGiftWishController {

    private final SiteContext siteContext;

    private final UserGiftWishService userGiftWishService;

    @ApiOperation("开箱心愿-查询")
    @GetMapping("/get-wish")
    public BaseResponse<UserGiftWishVO> getWish(@RequestParam Integer giftId) {
        Integer userId = siteContext.getCurrentUser().getId();

        UserGiftWishVO vo = userGiftWishService.getWish(userId, giftId);
        return RUtil.ok(vo);
    }

    @ApiOperation("开箱心愿-创建")
    @PostMapping("/create-wish")
    public BaseResponse<UserGiftWishVO> createWish(@Validated @RequestBody UserGiftWishVM vm) {
        Integer userId = siteContext.getCurrentUser().getId();

        UserGiftWishVO vo = userGiftWishService.create(userId, vm);
        return RUtil.ok(vo);
    }

    @ApiOperation("开箱心愿-取消")
    @PostMapping("/{id}/cancel-wish")
    public BaseResponse<Void> cancelWish(@PathVariable("id") Integer id) {
        Integer userId = siteContext.getCurrentUser().getId();

        userGiftWishService.cancelWish(userId, id);
        return RUtil.ok();
    }

    @ApiOperation("开箱心愿-领取")
    @PostMapping("/{id}/receive-wish")
    public BaseResponse<UserGiftWishRewardVO> receiveWish(@PathVariable("id") Integer id) {
        Integer userId = siteContext.getCurrentUser().getId();

        UserGiftWishRewardVO vo = userGiftWishService.receiveWish(userId, id);
        return RUtil.ok(vo);
    }

}
