package com.csgo.web.controller.fish;

import com.csgo.domain.plus.fish.FishUserJackpot;
import com.csgo.domain.plus.fish.FishUserJackpotBillRecord;
import com.csgo.domain.plus.fish.FishUserSpareJackpot;
import com.csgo.service.fish.FishUserJackpotService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.fish.FishUserJackpotResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 钓鱼玩法散户奖池
 */
@RestController
@RequestMapping("/fish/user/jackpot")
public class AdminFishUserJackpotController extends BackOfficeController {
    @Autowired
    private FishUserJackpotService fishUserJackpotService;

    @GetMapping
    @Log(desc = "获取奖池")
    public BaseResponse<FishUserJackpotResponse> get() {
        FishUserJackpot boxJackpot = fishUserJackpotService.getFishUserJackpot();
        FishUserSpareJackpot boxSpareJackpot = fishUserJackpotService.getFishUserSpareJackpot();
        FishUserJackpotResponse response = new FishUserJackpotResponse();
        if (boxJackpot != null) {
            response.setBalance(boxJackpot.getBalance());
        } else {
            response.setBalance(BigDecimal.ZERO);
        }
        if (boxSpareJackpot != null) {
            response.setSpareBalance(boxSpareJackpot.getBalance());
        } else {
            response.setSpareBalance(BigDecimal.ZERO);
        }
        return BaseResponse.<FishUserJackpotResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        fishUserJackpotService.updateFishUserJackpot(balance, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("pageList")
    @Log(desc = "奖池记录分页列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<FishUserJackpotBillRecord> fishUserJackpotBillRecordPageInfo = fishUserJackpotService.pageList(pageNum, pageSize);
        return new Result().result(fishUserJackpotBillRecordPageInfo);
    }
}
