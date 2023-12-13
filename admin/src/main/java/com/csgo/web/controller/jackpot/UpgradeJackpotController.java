package com.csgo.web.controller.jackpot;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.csgo.domain.plus.jackpot.UpgradeJackpot;
import com.csgo.domain.plus.jackpot.UpgradeJackpotBillRecord;
import com.csgo.domain.plus.jackpot.UpgradeSpareJackpot;
import com.csgo.service.jackpot.UpgradeJackpotService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.jackpot.UpgradeJackpotResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;

import io.swagger.annotations.ApiOperation;

/**
 * @author admin
 */
@RestController
@RequestMapping("/upgrade/jackpot")
public class UpgradeJackpotController extends BackOfficeController {
    @Autowired
    private UpgradeJackpotService upgradeJackpotService;

    @GetMapping
    @Log(desc = "获取奖池")
    public BaseResponse<UpgradeJackpotResponse> get() {
        UpgradeJackpot boxJackpot = upgradeJackpotService.getUpgradeJackpot();
        UpgradeSpareJackpot boxSpareJackpot = upgradeJackpotService.getUpgradeSpareJackpot();
        UpgradeJackpotResponse response = new UpgradeJackpotResponse();
        response.setBalance(boxJackpot.getBalance());
        response.setSpareBalance(boxSpareJackpot.getBalance());
        return BaseResponse.<UpgradeJackpotResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        upgradeJackpotService.updateUpgradeJackpot(balance, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("pageList")
    @ApiOperation("奖池记录分页列表")
    @Log(desc = "奖池记录分页列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<UpgradeJackpotBillRecord> boxJackpotBillRecordPageInfo = upgradeJackpotService.pageList(pageNum, pageSize);
        return new Result().result(boxJackpotBillRecordPageInfo);
    }
}
