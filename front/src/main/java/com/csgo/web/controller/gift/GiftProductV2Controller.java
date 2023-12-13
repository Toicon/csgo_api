package com.csgo.web.controller.gift;

import com.alibaba.fastjson.JSON;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.Gift;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.membership.Membership;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.service.GiftProductService;
import com.csgo.service.gift.GiftService;
import com.csgo.service.membership.MembershipService;
import com.csgo.service.pay.PayService;
import com.csgo.service.user.BalanceSupportService;
import com.csgo.service.user.UserPrizeService;
import com.csgo.support.GlobalConstants;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.SignUtil;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.gift.LotteryDrawRequest;
import com.csgo.web.response.gift.ShopGiftProductResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Api
@RequireSession
@RequestMapping("/gift/product")
@Slf4j
public class GiftProductV2Controller {
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private GiftService giftService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private UserPrizeService userPrizeService;
    @Autowired
    private PayService payService;
    @Autowired
    private BalanceSupportService balanceSupportService;
    @Autowired
    private MembershipService membershipService;

    /**
     * 最近掉落礼包商品信息
     *
     * @return
     */
    @GetMapping("/recent/{giftId}")
    public BaseResponse<List<ShopGiftProductResponse>> queryLatestByGiftId(@PathVariable("giftId") int giftId) {
        List<UserPrizePlus> userPrizeList = userPrizeService.recentByGiftId(giftId);
        Set<Integer> giftProductIds = userPrizeList.stream().map(UserPrizePlus::getGiftProductId).collect(Collectors.toSet());
        Map<Integer, GiftProductPlus> giftProductMap = giftProductService.findByGiftProductIds(giftProductIds).stream().collect(Collectors.toMap(GiftProductPlus::getId, giftProductPlus -> giftProductPlus));
        List<ShopGiftProductResponse> responses = userPrizeList.stream().filter(prize -> giftProductMap.containsKey(prize.getGiftProductId())).map(prize -> {
            GiftProductPlus giftProductPlus = giftProductMap.get(prize.getGiftProductId());
            ShopGiftProductResponse response = new ShopGiftProductResponse();
            BeanUtilsEx.copyProperties(giftProductPlus, response);
            response.setOutProbability(Integer.parseInt(prize.getGiftGradeG()));
            return response;
        }).collect(Collectors.toList());
        return BaseResponse.<List<ShopGiftProductResponse>>builder().data(responses).get();
    }

    @LoginRequired
    @PostMapping("/lottery-draw")
    public BaseResponse<List<ShopGiftProductResponse>> lotteryDraw(@Valid @RequestBody LotteryDrawRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        payService.validateDraw(userId);
        Map<String, Object> paramMap = new TreeMap<>();
        paramMap.put("id", request.getId());
        paramMap.put("userId", userId);
        paramMap.put("num", request.getNum());
        paramMap.put("key", GlobalConstants.SIGN_KEY);
        String sign = SignUtil.sign(paramMap);
        if (!sign.equals(request.getToken())) {
            log.error("接口参数验签校验失败 request:{}", JSON.toJSONString(request));
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        Gift gift = giftService.queryGiftId(request.getId());
        if (gift.getNewPeopleSwitch() && request.getNum() != 5) {
            log.error("五连必出金校验失败 wrong num:{}", request.getNum());
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        if (gift.getMembershipGrade() != null && gift.getMembershipGrade() > 0) {
            Membership membership = membershipService.findByUserId(userId);
            if (gift.getMembershipGrade() > membership.getGrade()) {
                throw new ApiException(HttpStatus.SC_BAD_REQUEST, "请先升级您的VIP等级");
            }
        }
        return BaseResponse.<List<ShopGiftProductResponse>>builder().data(balanceSupportService.lotteryDraw(userId, gift, request, sign)).get(); //涉及到余额变动，加用户全局锁
    }


}
