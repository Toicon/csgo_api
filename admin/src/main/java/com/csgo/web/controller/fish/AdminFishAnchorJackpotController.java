package com.csgo.web.controller.fish;

import com.csgo.domain.plus.fish.FishAnchorJackpot;
import com.csgo.domain.plus.fish.FishAnchorJackpotBillRecord;
import com.csgo.domain.plus.fish.FishAnchorSpareJackpot;
import com.csgo.service.fish.FishAnchorJackpotService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.response.fish.FishAnchorJackpotResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 钓鱼玩法测试奖池
 */
@RestController
@RequestMapping("/fish/anchor/jackpot")
public class AdminFishAnchorJackpotController extends BackOfficeController {
    @Autowired
    private FishAnchorJackpotService fishAnchorJackpotService;

    @GetMapping
    @Log(desc = "获取奖池")
    public BaseResponse<FishAnchorJackpotResponse> get() {
        FishAnchorJackpot boxJackpot = fishAnchorJackpotService.getFishAnchorJackpot();
        FishAnchorSpareJackpot boxSpareJackpot = fishAnchorJackpotService.getFishAnchorSpareJackpot();
        FishAnchorJackpotResponse response = new FishAnchorJackpotResponse();
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
        return BaseResponse.<FishAnchorJackpotResponse>builder().data(response).get();
    }

    @PutMapping
    @Log(desc = "更新奖池")
    public BaseResponse<Void> update(@RequestParam("balance") BigDecimal balance) {
        fishAnchorJackpotService.updateFishAnchorJackpot(balance, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("pageList")
    @Log(desc = "奖池记录分页列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<FishAnchorJackpotBillRecord> fishAnchorJackpotBillRecordPageInfo = fishAnchorJackpotService.pageList(pageNum, pageSize);
        return new Result().result(fishAnchorJackpotBillRecordPageInfo);
    }
}
