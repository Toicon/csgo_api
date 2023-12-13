package com.csgo.web.controller.jackpot;

import com.csgo.service.jackpot.LuckyProductJackpotService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.jackpot.LuckyProductJackpotResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Api
@RequestMapping("/lucky-product/jackpot")
public class LuckyProductJackpotController extends BackOfficeController {
    @Autowired
    private LuckyProductJackpotService jackpotService;

    @GetMapping
    @Log(desc = "获取饰品升级奖池")
    public BaseResponse<LuckyProductJackpotResponse> get() {
        LuckyProductJackpotResponse response = new LuckyProductJackpotResponse();
        response.setJackpot(jackpotService.getBalance());
        return BaseResponse.<LuckyProductJackpotResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新饰品升级奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        jackpotService.update(balance, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }
}
