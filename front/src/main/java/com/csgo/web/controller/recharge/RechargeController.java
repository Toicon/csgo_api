package com.csgo.web.controller.recharge;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.config.ExchangeRatePlus;
import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.recharge.RechargeMethod;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.ExchangeRateService;
import com.csgo.service.face.RealNameService;
import com.csgo.service.pay.IPaymentService;
import com.csgo.service.recharge.RechargeService;
import com.csgo.service.user.UserService;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.SignUtil;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.recharge.RechargeRequest;
import com.csgo.web.response.recharge.RechargeChannelPriceItemResponse;
import com.csgo.web.response.recharge.RechargeChannelResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Api
@LoginRequired
@RequireSession
public class RechargeController {

    @Autowired
    private UserService userService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private RealNameService realNameService;


   /* @GetMapping("/first/recharge")
    public BaseResponse<Map<String, String>> first(HttpServletRequest servletRequest) {
        Integer userId = siteContext.getCurrentUser().getId();
        if (StringUtils.hasText(redisTemplateFacde.get("RECHARGE" + userId))) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "请勿重复充值操作！");
        }
        redisTemplateFacde.set("RECHARGE_" + userId, "userId");

        boolean isApp = isApp(servletRequest);
        List<RechargeChannel> rechargeChannels = rechargeService.findAll(isApp);
        rechargeChannels = rechargeChannels.stream().filter(rechargeChannel -> rechargeChannel.getMethod().equals(RechargeMethod.ALI)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rechargeChannels)) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "当前渠道不可充值，请联系管理员");
        }
        RechargeChannel rechargeChannel = rechargeChannels.get(0);
        RechargeChannelPriceItem priceItem = rechargeService.findMinItem(rechargeChannel.getId());
        if (priceItem == null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值面额有误，请联系管理员");
        }

        UserPlus user = userService.get(userId);
        if (orderRecordService.findFirst(userId) != null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "您已不符合首充条件");
        }
        try {
            IPaymentService paymentService = rechargeService.getChannelByType(rechargeChannel.getType());
            //手机端支付
            if (isApp) {
                return BaseResponse.<Map<String, String>>builder().data(paymentService.mobilePay(user, rechargeChannel, priceItem, true, servletRequest)).get();
            }
            return BaseResponse.<Map<String, String>>builder().data(paymentService.pay(user, rechargeChannel, priceItem, true, servletRequest)).get();
        } finally {
            redisTemplateFacde.delete("RECHARGE_" + userId);
        }
    }*/

/*
    private Boolean isApp(HttpServletRequest request) {
        // 从浏览器获取请求头信息
        String info = request.getHeader("user-agent");
        return info.contains("Android") || info.contains("iPhone") || info.contains("iPad");
    }
*/

    @PostMapping("/recharge/qrcode")
    public BaseResponse<Map<String, String>> pay(@Valid @RequestBody RechargeRequest request, HttpServletRequest servletRequest) {
        RechargeChannel rechargeChannel = rechargeService.get(request.getChannelId());
        if (rechargeChannel == null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值渠道有误，请联系管理员");
        }
        Integer userId = siteContext.getCurrentUser().getId();
        UserPlus user = userService.get(userId);
        RechargeChannelPriceItem priceItem = rechargeService.getPriceItem(request.getPriceItemId());
        if (priceItem == null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值面额有误，请联系管理员");
        } else {
            //人脸识别
//            Map<String, String> result = this.checkIdentityFace(user, priceItem.getPrice(), request);
//            if (result != null && result.size() > 0) {
//                return BaseResponse.<Map<String, String>>builder().data(result).get();
//            }
            // 身份认证
            realNameService.checkRealNameVerifyPass(userId);
        }
        if (StringUtils.hasText(redisTemplateFacde.get("RECHARGE" + userId))) {
            throw BizClientException.of(CommonBizCode.COMMON_BUSY);
        }
        Map<String, Object> map = new TreeMap<>();
        map.put("userId", userId);
        map.put("channelId", request.getChannelId());
        map.put("priceItemId", request.getPriceItemId());
        map.put("key", GlobalConstants.SIGN_KEY);
        map.put("isApp", request.getIsApp());
        String stringSign = SignUtil.sign(map);
        if (!stringSign.equals(request.getToken())) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "接口参数验签校验失败");
        }
        redisTemplateFacde.set("RECHARGE" + userId, JSON.toJSONString(map));

        try {
            IPaymentService paymentService = rechargeService.getChannelByType(rechargeChannel.getType());
            //手机端支付
            if (null != request.getIsApp() && request.getIsApp()) {
                return BaseResponse.<Map<String, String>>builder().data(paymentService.mobilePay(user, rechargeChannel, priceItem, false, servletRequest)).get();
            }
            return BaseResponse.<Map<String, String>>builder().data(paymentService.pay(user, rechargeChannel, priceItem, false, servletRequest)).get();
        } finally {
            redisTemplateFacde.delete("RECHARGE" + userId);
        }
    }

    @GetMapping("/recharge/channel/{isApp}")
    public BaseResponse<List<RechargeChannelResponse>> findAll(@PathVariable(value = "isApp") boolean isApp) {
        List<RechargeChannel> rechargeChannels = rechargeService.findAll(isApp);
        List<Integer> channelIds = rechargeChannels.stream().map(RechargeChannel::getId).collect(Collectors.toList());
        Map<Integer, List<RechargeChannelPriceItem>> rechargeChannelPriceItemListMap = rechargeService.findPriceItemByChannelIds(channelIds).stream().collect(Collectors.groupingBy(RechargeChannelPriceItem::getChannelId));

        List<ExchangeRatePlus> exchangeRateList = exchangeRateService.findByFlag(1);
        if (CollectionUtils.isEmpty(exchangeRateList)) {
            throw new BusinessException(ExceptionCode.RATE_NOT_EXISTS);
        }
        ExchangeRatePlus exchangeRate = exchangeRateList.get(0);
        String rate = exchangeRate.getExchangeRate();
        List<RechargeChannelResponse> responses = rechargeChannels.stream().map(rechargeChannel -> {
            RechargeChannelResponse response = new RechargeChannelResponse();
            BeanUtils.copyProperties(rechargeChannel, response);
            if (!rechargeChannel.getMethod().equals(RechargeMethod.ALIAPP) && rechargeChannelPriceItemListMap.containsKey(rechargeChannel.getId())) {
                response.setPriceItems(rechargeChannelPriceItemListMap.get(rechargeChannel.getId()).stream()
                        .map(rechargeChannelPriceItem -> {
                            RechargeChannelPriceItemResponse itemResponse = new RechargeChannelPriceItemResponse();
                            BeanUtils.copyProperties(rechargeChannelPriceItem, itemResponse);
                            itemResponse.setPay(itemResponse.getPrice().multiply(BigDecimal.valueOf(Double.parseDouble(rate))).setScale(0, RoundingMode.HALF_UP));
                            return itemResponse;
                        }).sorted(Comparator.comparing(RechargeChannelPriceItemResponse::getPrice).thenComparing(RechargeChannelPriceItemResponse::getExtraPrice)).collect(Collectors.toList()));
            }
            response.setType(rechargeChannel.getType().name());
            response.setMethod(rechargeChannel.getMethod().name());
            return response;
        }).collect(Collectors.toList());
        return BaseResponse.<List<RechargeChannelResponse>>builder().data(responses).get();
    }


}
