package com.csgo.web.controller.jackpot;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.csgo.service.config.SystemConfigService;
import com.csgo.service.jackpot.JackpotService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.jackpot.JackpotResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;

/**
 * @author admin
 */
@Api
@RequestMapping("/jackpot")
public class JackpotController extends BackOfficeController {
    @Autowired
    private JackpotService jackpotService;
    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    @Log(desc = "获取奖池")
    public BaseResponse<JackpotResponse> get() {
        JackpotResponse response = new JackpotResponse();
        response.setJackpot(jackpotService.getBalance());
        response.setRate(new BigDecimal(systemConfigService.get("LUCKY_DRAW:LUCKY_JACKPOT_RATE").getValue()));
        response.calculate();
        return BaseResponse.<JackpotResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        jackpotService.update(balance, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }
}
