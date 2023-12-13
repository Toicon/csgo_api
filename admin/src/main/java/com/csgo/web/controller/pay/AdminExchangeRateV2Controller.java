package com.csgo.web.controller.pay;

import com.csgo.domain.plus.config.ExchangeRatePlus;
import com.csgo.service.ExchangeRateService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.config.UpdateExchangeRateRequest;
import com.csgo.web.response.config.ExchangeRateResponse;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author admin
 */
@Api
@RequestMapping("/rate")
public class AdminExchangeRateV2Controller extends BackOfficeController {
    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping
    public BaseResponse<ExchangeRateResponse> get() {
        ExchangeRatePlus rate = exchangeRateService.get();
        ExchangeRateResponse response = new ExchangeRateResponse();
        BeanUtils.copyProperties(rate, response);
        return BaseResponse.<ExchangeRateResponse>builder().data(response).get();
    }

    @PutMapping
    public BaseResponse<Void> update(@Valid @RequestBody UpdateExchangeRateRequest request) {
        ExchangeRatePlus exchangeRatePlus = exchangeRateService.get();
        exchangeRatePlus.setShopSpillPrice(request.getShopSpillPrice());
        exchangeRatePlus.setUt(new Date());
        exchangeRateService.update(exchangeRatePlus);
        return BaseResponse.<Void>builder().get();
    }
}
