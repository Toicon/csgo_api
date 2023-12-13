package com.csgo.web.controller.accessory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.validation.Valid;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.service.OrderRecordService;
import com.csgo.service.accessory.LuckyProductService;
import com.csgo.service.user.BalanceSupportService;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.accessory.LuckyProductRequest;
import com.csgo.web.response.lucky.UserLuckyHistoryResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Api
@LoginRequired
@RequireSession
@RequestMapping("/lucky/product")
@Slf4j
public class LuckyProductController {

    @Autowired
    private LuckyProductService luckyProductService;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private BalanceSupportService balanceSupportService;
    @Autowired
    private OrderRecordService orderRecordService;

    @PostMapping("/lottery-draw")
    public BaseResponse<UserLuckyHistoryResponse> lotteryDraw(@Valid @RequestBody LuckyProductRequest request) {
        List<OrderRecord> orderRecords = orderRecordService.findByUserIds(siteContext.getCurrentUser().getId());
        if (CollectionUtils.isEmpty(orderRecords)) {
            throw BizClientException.of(CommonBizCode.DRAW_LIMIT);
        }
        if (request.getLuckyValue().compareTo(BigDecimal.valueOf(5)) < 0) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        if (request.getLuckyValue().compareTo(BigDecimal.valueOf(75)) > 0) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        LuckyProductDTO luckyProduct = luckyProductService.get(request.getLuckyId());
        if (luckyProduct == null) {
            throw BizClientException.of(CommonBizCode.PRODUCT_NOT_FOUND);
        }
        BigDecimal pay = luckyProduct.getPrice().multiply(request.getLuckyValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (pay.compareTo(request.getPrice()) != 0) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        UserLuckyHistoryResponse response = balanceSupportService.luckyProduct(siteContext.getCurrentUser().getId(), request, pay, luckyProduct); //涉及到余额变动，加用户全局锁
        return BaseResponse.<UserLuckyHistoryResponse>builder().data(response).get();
    }


}
